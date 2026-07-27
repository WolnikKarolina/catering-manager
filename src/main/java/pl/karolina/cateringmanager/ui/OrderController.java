package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.*;
import pl.karolina.cateringmanager.service.OrderService;
import pl.karolina.cateringmanager.service.PriceService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class OrderController {

    private final OrderService os;
    private final Printer printer;
    private final DataReader reader;
    private final ClientController clientctrl;
    private final PriceService ps;
    private final LocalDate today = LocalDate.now();
    private final Menu menu;


    public OrderController(OrderService os, Printer printer, DataReader reader, ClientController clientctrl, PriceService ps, Menu menu) {
        this.os = os;
        this.printer = printer;
        this.reader = reader;
        this.clientctrl = clientctrl;
        this.ps = ps;
        this.menu = menu;
    }


    public void addOrder() {
        printer.print("--- Dodawanie nowego zamówienia ---");
        createOrders();
    }

    private void createOrders() {
        while (true) {
            Optional<Client> optionalClient = takeClient();
            if (optionalClient.isEmpty()) {
                return;
            }
            Client client = optionalClient.get();
            List<Order> activeOrders = os.findOrdersByClientFromDate(client, today);
            if (!activeOrders.isEmpty()) {
                printer.print("Klient posiada aktywne zamówienia");
            }
            int choice = reader.readPositiveNumber("1 - złóż zamówienie / 2 - Wróć do poprzedniego menu");
            switch (choice) {
                case 1 ->{
                    Optional <OrderData> optionalOrderData = getOrderData();
                    if (optionalOrderData.isEmpty()) {
                        return;
                    }
                    OrderData orderData = optionalOrderData.get();
                    processOrderChoice(client, orderData);
                }
                case 2 -> {
                    return;
                }
            }
        }
    }

    private void processOrderChoice(Client client, OrderData orderData) {
        int choice = menu.getProcessOrderChoice();
        switch (choice) {
            case 1 -> addDates(orderPerDay(), client, orderData);
            case 2 -> addDates(ordersFromRange(day -> day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY), client, orderData);
            case 3 -> addDates(ordersFromRange(day -> day != DayOfWeek.SUNDAY), client, orderData);
            case 4 -> addDates(ordersFromRange(day -> true), client, orderData);
            case 5 -> {return;}
            default -> printer.print("Wybrano niepoprawną liczbę, wybierz 1 - 5");
        }
    }

    private Optional<OrderData> getOrderData() {
        Calories calories = readCalories();
        DietType dietType = readDietType();
        Double discount = readDiscount();
        Optional<Price> price = readPrice(calories);
        return price.map(value -> new OrderData(calories, dietType, discount, value));
    }

    private record OrderData(Calories calories, DietType dietType, Double discount, Price price) {
    }

    public void printOrders() {
        Optional<Client> optionalClient = takeClient();
        if (optionalClient.isEmpty()) {
            printer.print("Nie znaleziono klienta");
            return;
        }
        Client client = optionalClient.get();
        LocalDate from = reader.readDate("Wprowadź datę od której chcesz zobaczyć zamówienia");
        List<Order> filtered = os.findOrdersByClientFromDate(client, from);
        if (filtered.isEmpty()) {
            printer.print("Brak zamówień dla danego klienta");
        } else {
            filtered.forEach(printer::print);
        }
    }


    public void editOrders() {
       int choice = menu.getEditOrderChoice();
        switch (choice) {
            case 1 -> editSingleOrder();
            case 2 -> editOrdersByTime();
        }
    }

    private void editOrdersByTime() {
        Optional<Client> optionalClient = takeClient();
        if (optionalClient.isEmpty()) {
            printer.print("Nie znaleziono klienta");
            return;
        }
        Client client = optionalClient.get();
        LocalDate startDate = reader.readDate("Podaj date początkową zamówienie które chcesz edytować");
        LocalDate finishDate = reader.readDate("Podaj datę końcową");
        if (validationDataRange(startDate, finishDate)) return;
        List<Order> ordersByDate = os.findOrdersByDate(client.getId(), startDate, finishDate);
        ordersByDate.forEach(printer::print);
        applyEditToAll(ordersByDate);
        printer.print("Zamówienia zmienione");
    }

    private void applyEditToAll(List<Order> ordersByDate) {
        int choice = menu.getOptionsToEditChoice();
        switch (choice) {
            case 1 -> {
                Calories newCalories = readCalories();
                for (Order order : ordersByDate) {
                    order.setCalories(newCalories);
                    os.updateOrder(order);
                }
            }
            case 2 -> {
                DietType newDietType = readDietType();
                for (Order order : ordersByDate) {
                    order.setDietType(newDietType);
                    os.updateOrder(order);
                }
            }
            case 3 -> {
                double newDiscount = readDiscount();
                for (Order order : ordersByDate) {
                    order.setDiscount(newDiscount);
                    os.updateOrder(order);
                }
            }
            default -> throw new IllegalArgumentException("Niepoprawny wybór " + choice);
        }
    }

    private boolean validationDataRange(LocalDate startDate, LocalDate finishDate) {
        if (startDate.isBefore(today)) {
            String choice = reader.readText("Edytujesz zrealizowane zamówienie, czy chcesz kontynuować? t/n").trim();
            if (choice.equalsIgnoreCase("n")) return true;
        }
        if (finishDate.isBefore(startDate)) {
            printer.print("Data końcowa jest przed datą początkową, spróbuj ponownie");
            return true;
        }
        if (startDate.getYear() != today.getYear() && finishDate.getYear() != today.getYear()) {
            String choice = reader.readText("Zamówienie wykracza poza bieżący rok czy chcesz kontynuować t/n").trim();
            if (choice.equalsIgnoreCase("n")) return true;
        }
        return false;
    }

    private void applyEdit (Order order, int choice) {
        switch (choice) {
            case 1 -> order.setCalories(readCalories());
            case 2 -> order.setDietType(readDietType());
            case 3 -> order.setDiscount(readDiscount());
            default -> throw new IllegalArgumentException("Nieprawidłowa warość " + choice);
        }
        os.updateOrder(order);
        printer.print("Zamówienie zmienione");
    }

    public void editSingleOrder() {
        printOrders();
        int orderId = reader.readPositiveNumber("Wpisz nr zamówienia które chcesz edytować");
        Order order = os.findOrderById(orderId).orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        int choice = menu.getOptionsToEditChoice();
        applyEdit(order, choice);
    }

    public void deleteOrder() {
        Optional<Client> client = takeClient();
        if (client.isEmpty()) {
            printer.print("Klient nie istnieje");
            return;
        }
        LocalDate startDate = reader.readDate("Podaj date początkową zamówienie które chcesz edytować");
        LocalDate finishDate = reader.readDate("Podaj datę końcową");
        List<Order> ordersByDate = os.findOrdersByDate(client.get().getId(), startDate, finishDate);
        if (ordersByDate.isEmpty()) {
            printer.print("Brak zamówień w podanym przedziale");
            return;
        }
        for (Order order : ordersByDate) {
            os.deleteOrder(order.getId());
        }
        printer.print("Usunięto zamówienia w okresie: " + startDate + " - " + finishDate);
    }


    private Optional<Client> takeClient() {
        clientctrl.printClient();
        while (true) {
            int id = reader.readPositiveNumber("Wprowadź id klienta");
            Optional<Client> client = clientctrl.findClientById(id);
            if (client.isPresent()) {
                return client;
            }
            String choice = reader.readText("Chcesz spróbować ponownie? t/n").trim();
            if (choice.equalsIgnoreCase("n")) {
                return Optional.empty();
            }
            if (!choice.equalsIgnoreCase("t")) {
                printer.print("Wybrano złą literę");
            }
        }
    }

    private Optional<Price> readPrice(Calories calories) {
        Optional<Price> price = ps.findByCalories(calories.getKcal());
        if (price.isEmpty()) {
            printer.print("Brak ceny, popraw dane w systemie i spróbuj ponownie");
        }
        return price;
    }

    private Calories readCalories() {
        while (true) {
            int kcal = reader.readPositiveNumber("Wpisz kaloryczność");
            try {
                return Calories.fromKcal(kcal);
            } catch (IllegalArgumentException e) {
                printer.print("Niepoprawna kaloryczność, spróbuj ponownie");
                printer.printAll(Calories.getAll());
            }
        }
    }

    private Double readDiscount() {
        while (true) {
            String disc = reader.readText("Ile procent rabatu ma otrzymać klient?").trim();
            try {
                double discount = Double.parseDouble(disc);
                if (discount < 0 || discount > 100) {
                    printer.print("Rabat musi wynosić od 0 do 100");
                    continue;
                }
                printer.print("Dodano rabat: " + discount + "%");
                return discount;
            } catch (NumberFormatException e) {
                printer.print("Rabat musi wynosić od 0 do 100");
            }
        }
    }

    private DietType readDietType() {
        while (true) {
            int choice = reader.readPositiveNumber("1 - STANDARD, 2 - WEGE");
            switch (choice) {
                case 1 -> {
                    return DietType.STANDARD;
                }
                case 2 -> {
                    return DietType.WEGE;
                }
                default -> printer.print("Niepoprawny wybór, wpisz 1 lub 2");
            }
        }
    }

    public List<LocalDate> ordersFromRange(Predicate<DayOfWeek> filter) {
        while (true) {
            List<LocalDate> dates = new ArrayList<>();
            LocalDate startDate = reader.readDate("Podaj date początkową");
            LocalDate finishDate = reader.readDate("Podaj datę końcową");

            for (LocalDate d = startDate; !d.isAfter(finishDate); d = d.plusDays(1)) {
                if (filter.test(d.getDayOfWeek())) {
                    dates.add(d);
                }
            }
            if (printDatesToAccept(dates)) {
                return dates;
            } else {
                String choice = reader.readText(" Chcesz spróbować ponownie? t/n").trim();
                if (choice.equalsIgnoreCase("t")) {
                    continue;
                } else return new ArrayList<>();
            }
        }
    }

    private void addDates(List<LocalDate> dates, Client client,OrderData orderData) {
        if (dates.isEmpty()) {
            printer.print("Nie dodano zamówień");
            return;
        }
        for (LocalDate d : dates) {
            Order order = new Order(client.getId(), client, d, orderData.calories, orderData.dietType, orderData.discount, orderData.price.getPrice());
            os.addOrder(order);
        }
        printer.print("Dodano " + dates.size() + " zamówień");
    }

    private List<LocalDate> orderPerDay() {
        List<LocalDate> dates = new LinkedList<>();
        while (true) {
            LocalDate date = reader.readDate("Podaj datę");
            if (date.isBefore(today)) {
                String choice = reader.readText("Podana data juz była, czy dodac mimo to? t/n");
                if (choice.equalsIgnoreCase("n")) {
                    continue;
                }
                if (!choice.equalsIgnoreCase("t")) {
                    printer.print("Niepoprawny wybór");
                    continue;
                }
            }
            dates.add(date);
            String choice = reader.readText("Czy dodać kolejny dzień? t/n").trim();
            if (choice.equalsIgnoreCase("n")) {
                if (printDatesToAccept(dates)) {
                    return dates;
                }
                dates.clear();
            }
        }
    }

    private boolean printDatesToAccept(List<LocalDate> dates) {
        printer.printAll(dates);
        String accept = reader.readText("Czy zamówienie się zgadza? t/n").trim();
        return accept.equalsIgnoreCase("t");
    }

}
