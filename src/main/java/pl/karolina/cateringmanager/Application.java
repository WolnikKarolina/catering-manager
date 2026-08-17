package pl.karolina.cateringmanager;

import pl.karolina.cateringmanager.repository.ClientRepository;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PaymentRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;
import pl.karolina.cateringmanager.service.ClientService;
import pl.karolina.cateringmanager.service.OrderService;
import pl.karolina.cateringmanager.service.PaymentService;
import pl.karolina.cateringmanager.service.PriceService;
import pl.karolina.cateringmanager.ui.*;

public class Application {

    public void start() {
        Printer printer = new Printer();
        DataReader reader = new DataReader(printer);
        Menu menu = new Menu(printer, reader);

        ClientRepository clientRepository = new ClientRepository();
        OrderRepository orderRepository = new OrderRepository();
        PriceRepository priceRepository = new PriceRepository();
        PaymentRepository paymentRepository = new PaymentRepository();

        ClientService clientService = new ClientService(clientRepository);
        PriceService priceService = new PriceService(priceRepository);
        OrderService orderService = new OrderService(orderRepository, priceRepository, clientRepository);
        PaymentService paymentService = new PaymentService(paymentRepository);

        ClientController clientctrl =
                new ClientController(clientService, reader, printer, menu);

        PriceController pricectrl =
                new PriceController(priceService, reader, printer);

        OrderController orderctrl =
                new OrderController(orderService, printer, reader, clientctrl, priceService, menu);

        PaymentController paymentctrl = new PaymentController(paymentService, printer, reader, menu, clientctrl, orderService);

        MainMenu mainMenu = new MainMenu(reader, printer, clientctrl, pricectrl, orderctrl, paymentctrl );

        mainMenu.run();
    }
}
