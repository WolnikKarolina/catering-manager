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
            int choice = reader.readPositiveNumber("Wybierz opcję: \n 1 - Klienci \n 2 - Zamówienia \n 3 - Ceny \n 4 - Raporty \n 5 - Płatności \n 6 - Wyjście z programu");
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
            int choice = reader.readPositiveNumber("Wybierz opcję: \n 1 - Wyświetl cennik \n 2 - Zmień ceny \n 3 - Powrót do poprzedniego menu"  );
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
            int choice = reader.readPositiveNumber("Wybierz opcję: \n 1 - Dodaj nowe zamówienie \n 2 - Wyświetl zamówienia klienta \n 3 - Edytuj zamówienie \n 4 - Usuń zamówienie \n 5 - Wyjście do poprzedniego menu");
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
        int choice = reader.readPositiveNumber("Wybierz opcję: \n 1 - Edytuj zamówienie \n 2 - Usuń zamówienie \n 3 - Powrót do poprzedniego menu");
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
            int choice = reader.readPositiveNumber("Wybierz opcję: \n 1 - Wyświetl wszystkich klientów \n 2 - Wyszukaj klienta \n 3 - Dodaj nowego klienta \n 4 - Edytuj/Unuń \n 5 - Wyjście do poprzedniego menu");
            switch (choice) {
                case 1 -> clientctrl.printAllClients();
                case 2 -> findClient();
                case 3 -> clientctrl.addClient();
                case 4 -> findOrDeleteClient();
                case 5 -> {
                    return;
                }
                default -> printer.print("Niepoprawna opcja spróbuj ponownie");
            }
        }
    }

    private void findOrDeleteClient() {
        while (true) {
            int choice = reader.readPositiveNumber("Wybierz opcję: \n 1 - Edytuj dane \n 2 - Usuń klienta \n 3 - Powrót do poprzedniego menu");
            switch (choice) {
                case 1 -> clientctrl.updateClientData();
                case 2 -> clientctrl.deleteClient();
                case 3 -> {
                    return;
                }
                default -> printer.print("Niepoprawny wybór, spróbuj ponownie");
            }
        }
    }

    private void findClient() {
        while (true) {
            clientctrl.printClient();
            String choice = reader.readText("Czy chcesz edytować dane? t/n");
            if (choice.equals("t")) {
                clientctrl.updateClientData();
            } else {
                return;
            }
        }
    }
}
