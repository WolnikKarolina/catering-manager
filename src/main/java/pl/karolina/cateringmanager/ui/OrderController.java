package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.*;
import pl.karolina.cateringmanager.service.OrderService;
import pl.karolina.cateringmanager.service.PriceService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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
            int id = client.get().getId();
            Calories calories = readCalories();
            DietType dietType = readDietType();
            Double discount = readDiscount();
            Price price = readPrice(calories);
            if (price == null) {
                return;
            }
            int choice = reader.readPositiveNumber("1 - zamównie na pojedyncze dni \n 2 - zmównie w dni robocze \n 3 - zamównie z sobotami \n 4 - zamównie razem z weekedami \n 5 - Wróć do poprzedniego menu");
            switch (choice) {
                case 1 -> {
                    List<LocalDate> dates = orderPerDay();
                    addDates(dates, id, client, calories, dietType, discount, price);
                }
                case 2 -> {
                    List<LocalDate> dates = ordersFromRange(day -> day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY);
                    addDates(dates, id, client, calories, dietType, discount, price);
                }
                case 3 -> {
                    List<LocalDate> dates = ordersFromRange(day -> day != DayOfWeek.SUNDAY);
                    addDates(dates, id, client, calories, dietType, discount, price);
                }
                case 4 -> {
                    List<LocalDate> dates = ordersFromRange(day -> true);
                    addDates(dates, id, client, calories, dietType, discount, price);
                }
                case 5 -> {
                    return;
                }
                default -> printer.print("Wybrano niepoprawną liczbę, wybierz 1 - 5");
            }
            String again = reader.readText("Czy chesz złożyc kolejne zamówienie? t/n");
            if (again.equals("n")) {
                return;
            }
        }
    }

    private Optional<Client> takeClient() {
        clctr.getClient();
        while (true) {
            int id = reader.readPositiveNumber("Wprowadz id klienta dla którego chcesz złożyć zamówienie");
            Optional<Client> client = clctr.findClientById(id);
            if (client.isPresent()) {
                return client;
            }
            String choice = reader.readText("Chcesz spróbować ponownie? t/n");
            if (choice.equals("n")) {
                return Optional.empty();
            } else if (choice.equals("t")) {
                continue;
            } else {
                printer.print("Wybrano złą literę");
                continue;
            }
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

    private void addDates(List<LocalDate> dates, int id, Optional<Client> client, Calories calories, DietType dietType, Double discount, Price price) {
        if (dates.isEmpty()) {
            printer.print("Nie dodano zamówień");
            return;
        }
        for (LocalDate d : dates) {
            Order order = new Order(id, client.get(), d, calories, dietType, discount, price.getPrice());
            os.addOrder(order);
        }
        printer.print("Dodano " + dates.size() + " zamówień");
    }

    private List<LocalDate> orderPerDay() {
        List<LocalDate> dates = new ArrayList<>();
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
