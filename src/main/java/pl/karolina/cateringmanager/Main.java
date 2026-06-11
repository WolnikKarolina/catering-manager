package pl.karolina.cateringmanager;

import pl.karolina.cateringmanager.model.*;
import pl.karolina.cateringmanager.repository.ClientRepository;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;
import pl.karolina.cateringmanager.ui.MainMenu;

import java.time.LocalDate;
import java.util.List;

public class Main {
    static void main(String[] args) {
        System.out.println("Catering manager");

        MainMenu menu = new MainMenu();
        menu.run();


    }
}
