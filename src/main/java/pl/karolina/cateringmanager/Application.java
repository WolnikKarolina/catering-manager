package pl.karolina.cateringmanager;

import pl.karolina.cateringmanager.repository.ClientRepository;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;
import pl.karolina.cateringmanager.service.ClientService;
import pl.karolina.cateringmanager.service.OrderService;
import pl.karolina.cateringmanager.service.PriceService;
import pl.karolina.cateringmanager.ui.*;

public class Application {

    public void start() {
        Printer printer = new Printer();
        DataReader reader = new DataReader(printer);
        Menu menu = new Menu(printer, reader);

        ClientRepository cr = new ClientRepository();
        OrderRepository or = new OrderRepository();
        PriceRepository pr = new PriceRepository();

        ClientService cs = new ClientService(cr);
        PriceService ps = new PriceService(pr);
        OrderService os = new OrderService(or, pr);

        ClientController clientctrl =
                new ClientController(cs, reader, printer);

        PriceController pricectrl =
                new PriceController(ps, reader, printer);

        OrderController orderctrl =
                new OrderController(os, printer, reader, clientctrl, ps, menu);

        MainMenu mainMenu = new MainMenu(reader, printer, clientctrl, pricectrl, orderctrl );

        mainMenu.run();
    }
}
