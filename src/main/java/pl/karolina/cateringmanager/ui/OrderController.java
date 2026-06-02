package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.*;
import pl.karolina.cateringmanager.service.OrderService;
import pl.karolina.cateringmanager.service.PriceService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class OrderController {

    private final OrderService os;
    private final Printer printer;
    private final DataReader reader;
    private final ClientController clctr;
    private final PriceService ps;


    public OrderController(OrderService os, Printer printer, DataReader reader, ClientController clctr, PriceService ps) {
        this.os = os;
        this.printer = printer;
        this.reader = reader;
        this.clctr = clctr;
        this.ps = ps;
    }

    public void addOrder() {
        printer.print("Dodawanie nowego zamówienia");
        createOrders();
    }

    private void createOrders() {
        while (true) {
            Optional<Client> client = takeClient();
            if (client.isEmpty()) {
                return;
            }
            OrderData orderData = getOrderData();
            if (orderData == null) return;
            if (processOrderChoice(client.get(), orderData)) return;
            String again = reader.readText("Czy chcesz złożyć kolejne zamówienie? t/n");
            if (again.equals("n")) {
                return;
            }
        }
    }

    private boolean processOrderChoice(Client client, OrderData orderData) {
        int choice = reader.readPositiveNumber("1 - zamównie na pojedyncze dni \n 2 - zamównie na dni robocze \n 3 - zamówienie z sobotami \n 4 - zamówienie razem z weekedami \n 5 - Wróć do poprzedniego menu");
        List<LocalDate> dates = switch(choice) {

            case 1 -> orderPerDay();
            case 2 -> ordersFromRange(day -> day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY);
            case 3 -> ordersFromRange(day -> day != DayOfWeek.SUNDAY);
            case 4 -> ordersFromRange(day -> true);
            case 5 -> null;
            default -> {
                printer.print("Wybrano niepoprawną liczbę, wybierz 1 - 5");
                yield null;
            }
        };
        if (dates != null) {
            addDates(dates, client, orderData);
        }
        return choice == 5;
    }

    private OrderData getOrderData() {
        Calories calories = readCalories();
        DietType dietType = readDietType();
        Double discount = readDiscount();
        Price price = readPrice(calories);
        if (price == null) {
            return null;
        }
        OrderData orderData = new OrderData(calories, dietType, discount, price);
        return orderData;
    }

    private record OrderData(Calories calories, DietType dietType, Double discount, Price price) {
    }

    public void printOrders () {
        Optional<Client> client = takeClient();
        if (client.isEmpty()) {
            printer.print("Klient nie istnieje");
            return;
        }
        List<Order> orders = os.findOrderByClientId(client.get().getId());
        if (orders.isEmpty()) {
            printer.print("Brak zamówień dla danego klienta");
            return;
        } else {
            orders.forEach(System.out::println);
        }
    }
    
    public void editOrders() {
        Optional<Client> client = takeClient();
        if (client.isEmpty()) {
            printer.print("Klient nie istnieje");
            return;
        }

        LocalDate startDate = reader.readDate("Podaj date początkową zamówienie które chcesz edytować");
        LocalDate finishDate = reader.readDate("Podaj datę końcową");
        List<Order> ordersByDate = os.findOrdersByDate(client.get().getId(), startDate, finishDate);
        int choice = reader.readPositiveNumber("Co chcesz edytować? \n 1 - Kalorie \n 2 - Typ diety \n  3 - rabat");
        for (Order order : ordersByDate) {
        applyEdit(order, choice);
        }

    }

    private void applyEdit (Order order, int choice) {
        switch (choice) {
            case 1 -> order.setCalories(readCalories());
            case 2 -> order.setDietType(readDietType());
            case 3 -> order.setDiscount(readDiscount());
            default -> throw new IllegalArgumentException("Unexpected value: " + choice);
        }
        os.updateOrder(order);
        printer.print("Zamówienie zmienione");
    }

    public void editOrder() {
        printOrders();
        int orderId = reader.readPositiveNumber("Wpisz nr zamówienie które chcesz edytować");
        Order order = os.findOrderById(orderId);
        int choice = reader.readPositiveNumber("Co chcesz edytować? \n 1 - Kalorie \n 2 - Typ diety \n  3 - rabat");
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
            printer.print("Brak zamówień w podanym przedizale");
            return;
        }
        for (Order order : ordersByDate) {
            os.deleteOrder(order.getId());
        }
        printer.print("Usunięto zamówienia w okresie: " + startDate + " - " + finishDate);
    }


    private Optional<Client> takeClient() {
        clctr.printClient();
        while (true) {
            int id = reader.readPositiveNumber("Wprowadz id klienta");
            Optional<Client> client = clctr.findClientById(id);
            if (client.isPresent()) {
                return client;
            }
            String choice = reader.readText("Chcesz spróbować ponownie? t/n");
            if (choice.equals("n")) {
                return Optional.empty();
            }
            if (!choice.equalsIgnoreCase("t"))
                printer.print("Wybrano złą literę");
        }
    }

    private Price readPrice(Calories calories) {
        try {
            return ps.findByCalories(calories.getKcal()).orElseThrow(() -> new RuntimeException("Brak ceny"));
        } catch (RuntimeException e) {
            printer.print("Brak ceny, popraw dane w systemie i spróbuj ponownie");
            return null;
        }
    }

    private Calories readCalories() {
        while (true) {
            int kcal = reader.readPositiveNumber("Wpisz kaloryczność");
            try {
                return Calories.fromKcal(kcal);
            } catch (IllegalArgumentException e) {
                printer.print("Niepoprawna kaloryczność, spróbuj ponownie");
            }
        }
    }

    private Double readDiscount() {
        while (true) {
            String disc = reader.readText("Ile procent rabatu ma otrzymać klient?");
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
                String choice = reader.readText(" Chcesz spróbować ponownie? t/n");
                if (choice.equals("t")) {
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
            dates.add(date);
            String choice = reader.readText("Czy dodać kolejny dzień? t/n");
            if (choice.equals("n")) {
                if (printDatesToAccept(dates)) {
                    return dates;
                }
                dates.clear();
            }
        }
    }

    private boolean printDatesToAccept(List<LocalDate> dates) {
        printer.print(dates);
        String accept = reader.readText("Czy zamówienie się zgadza? t/n");
        return accept.equals("t");
    }

}
