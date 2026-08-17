package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.model.Payment;
import pl.karolina.cateringmanager.model.PaymentMethod;
import pl.karolina.cateringmanager.model.SettlementMode;
import pl.karolina.cateringmanager.service.OrderService;
import pl.karolina.cateringmanager.service.PaymentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PaymentController {

    private final PaymentService paymentService;
    private final Printer printer;
    private final DataReader reader;
    private final Menu menu;
    private final ClientController clientController;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, Printer printer, DataReader reader, Menu menu, ClientController clientController, OrderService orderService) {
        this.paymentService = paymentService;
        this.printer = printer;
        this.reader = reader;
        this.menu = menu;
        this.clientController = clientController;
        this.orderService = orderService;
    }

    public void addPayment() {
        printClients();
        int clientId = reader.readPositiveNumber("Wprowadź id klienta dla którego chcesz wprowadzić płatość");
        Optional<Client> client = clientController.findClientById(clientId);
        if (client.isEmpty()) {
            printer.print("Klient nie istnieje");
            return;
        }
        Client c = client.get();
        double amount = (double) reader.readPositiveNumber("Wprowadź kwotę");
        LocalDate date = reader.readDate("Wprowadz datę płatności");
        choosePaymentMethod(amount, date, clientId);
        settlePayment(c, amount);
    }

    private void printClients() {
        List<Client> clients = clientController.searchClientWithRetry();
        printer.print(clients);
    }

    private void choosePaymentMethod(double amount, LocalDate date, int clientId) {
        boolean waitingForValidChoice = true;
        while (waitingForValidChoice) {
            printer.print("Wybierz sposób płatności");
            int choice = reader.readPositiveNumber(PaymentMethod.CASH.getDescription() + " - 1, " + PaymentMethod.TRANSFER.getDescription() + "- 2");
            switch (choice) {
                case 1 -> {
                    paymentService.addPayment(new Payment(amount, date, clientId, PaymentMethod.CASH));
                    waitingForValidChoice = false;
                }
                case 2 -> {
                    paymentService.addPayment(new Payment(amount, date, clientId, PaymentMethod.TRANSFER));
                    waitingForValidChoice = false;
                }
                    default -> printer.print("Niepoprawny wybór, wybierz 1 lub 2");
            }
        }
    }

    private void settlePayment(Client c, double amount) {
        printer.print("Wybierz sposób rozksięgowania płatności: ");
        int choice = reader.readPositiveNumber(SettlementMode.OLDEST_UNPAID_FIRST.getDescription() + " - 1, "
                + SettlementMode.SPECIFIC_PERIOD.getDescription() + " - 2");

        switch (choice) {
            case 1 -> orderService.settleOldestFirst(c, amount);
            case 2 -> settleSpecificPeriod(c, amount);
        }
    }

    private void settleSpecificPeriod(Client c, double amount) {
        boolean ifDateIsOk = true;
        LocalDate startDate = null;
        LocalDate finishDate = null;
        while (ifDateIsOk) {
             startDate = reader.readDate("Podaj datę początkową zamówienia które chcesz rozksięgować");
             finishDate = reader.readDate("Podaj datę końcową");
            if (startDate.isAfter(finishDate)) {
                printer.print("Data początkowa nie może byc późniejsza niż końcowa");
            } else {
                ifDateIsOk = false;
            }
        }
        orderService.settleByPeriod(c, amount, startDate, finishDate);
    }


}
