package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.repository.ClientRepository;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;
import pl.karolina.cateringmanager.service.ClientService;
import pl.karolina.cateringmanager.service.OrderService;
import pl.karolina.cateringmanager.service.PriceService;


public class MainMenu {
    private final DataReader reader;
    private final Printer printer;
    private final ClientController clientctrl;
    private final OrderController orderctrl;

    public MainMenu() {
        this.printer = new Printer();
        this.reader = new DataReader(printer);
        ClientRepository cr = new ClientRepository();
        OrderRepository or = new OrderRepository();
        PriceRepository pr = new PriceRepository();
        ClientService cs = new ClientService(cr);
        OrderService os = new OrderService(or, pr);
        PriceService ps = new PriceService(pr);
        this.clientctrl = new ClientController(cs, reader, printer);
        this.orderctrl = new OrderController(os, printer, reader, clientctrl, ps);
    }

    public void run() {
        while (true) {
            printer.print("1 - Klienci");
            printer.print("2 - Zamówienia");
            printer.print("3 - Ceny");
            printer.print("4 - Raporty");
            printer.print("5 - Płatności");
            printer.print("6 - Wyjście z programu");
            int choice = reader.readPositiveNumber("Wybierz opcję");
            switch (choice) {
                case 1 -> clientsMenu();
                case 2 -> ordersMenu();
                case 3 -> priceMenu();
                case 4 -> dailyMenu();
                case 5 -> paymentsMenu();
                case 6 -> {
                    printer.print("Wyjście z programu");
                    reader.close();
                    return;
                }
                default -> printer.print("Wybrałes niepoprawną opcję");
            }
        }
    }

    private void paymentsMenu() {
    }

    private void dailyMenu() {
        
    }

    private void priceMenu() {
        while (true) {
            printer.print("1 - Wyświetl cennik");
            printer.print("2 - Zmień ceny");
            printer.print("3 - Powrót do poprzedniego menu");
            int choice = reader.readPositiveNumber("Wybierz opcję");
            switch (choice) {
                case 1 -> {}
                case 2 -> {}
                case 3 -> {
                    return;
                }
                default -> printer.print("Niepoprawna opcja, spróbuj ponownie");
            }

        }
    }

    private void ordersMenu() {
        while (true) {
            printer.print("1 - Dodaj nowe zamówienie");
            printer.print("2 - Wyświetl zamówienia klienta");
            printer.print("3 - Edytuj zamówienie");
            printer.print("4 - Usuń zamówienie");
            printer.print("5 - Wyjście do poprzedniego menu");
            int choice = reader.readPositiveNumber("Wybierz opcję");
            switch (choice) {
                case 1 -> orderctrl.addOrder();
                case 2 -> printOrders();
                case 3 -> orderctrl.editOrders();
                case 4 -> orderctrl.deleteOrder();
                case 5 -> {
                    return;
                }
                default -> printer.print("Niepoprawna opcja spróbuj ponownie");
            }
        }
    }

    private void printOrders() {
        orderctrl.printOrders();
        printer.print("1 - Edytuj zamówienie");
        printer.print("2 - Usuń zamówienie");
        printer.print("3 - Powrót do poprzedniego menu");
        int choice = reader.readPositiveNumber("Wybierz opcję");
        switch (choice) {
            case 1 -> orderctrl.editOrders();
            case 2 -> orderctrl.deleteOrder();
            case 3 -> {
                return;
            }
            default -> printer.print("Niepoprawna opcja, spróbuj ponownie");
        }
    }

    private void clientsMenu() {
        while (true) {
            printer.print("1 - Wyświetl wszystkich klientów");
            printer.print("2 - Wyszukaj klienta");
            printer.print("3 - Dodaj nowego klienta");
            printer.print("4 - Edytuj");
            printer.print("5 - Wyjście do poprzedniego menu");
            int choice = reader.readPositiveNumber("Wybierz opcję");
            switch (choice) {
                case 1 -> clientctrl.printAllClients();
                case 2 -> clientctrl.printClient();
                case 3 -> clientctrl.addClient();
                case 4 -> clientctrl.updateClientData();
                case 5 -> {
                    return;
                }
                default -> printer.print("Niepoprawna opcja spróbuj ponownie");
            }
        }
    }

}
