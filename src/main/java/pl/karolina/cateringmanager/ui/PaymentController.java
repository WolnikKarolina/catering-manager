package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.model.Payment;
import pl.karolina.cateringmanager.model.PaymentMethod;
import pl.karolina.cateringmanager.service.PaymentService;

import java.time.LocalDate;
import java.util.List;

public class PaymentController {

    private final PaymentService paymentService;
    private final Printer printer;
    private final DataReader reader;
    private final Menu menu;
    private final ClientController clientController;

    public PaymentController(PaymentService paymentService, Printer printer, DataReader reader, Menu menu, ClientController clientController) {
        this.paymentService = paymentService;
        this.printer = printer;
        this.reader = reader;
        this.menu = menu;
        this.clientController = clientController;
    }

    public void addPayment() {
        List<Client> clients = clientController.searchClientWithRetry();
        printer.print(clients);
        int clientId = reader.readPositiveNumber("Wprowadź id klienta dla którego chcesz wprowadzić płatość");
        double amount = (double) reader.readPositiveNumber("Wprowadź kwotę");
        LocalDate date = reader.readDate("Wprowadz datę płatności");
        while (true) {
            int choice = reader.readPositiveNumber("Płatność gotówką - 1, Płatnośc przewem - 2");
            switch (choice) {
                case 1 -> paymentService.addPayment(new Payment(amount, date, clientId, PaymentMethod.CASH));
                case 2 -> paymentService.addPayment(new Payment(amount, date, clientId, PaymentMethod.TRANSFER));
                default -> printer.print("Niepoprawny wybór, wybierz 1 lub 2");
            }
        }
    }
}
