package pl.karolina.cateringmanager;

import pl.karolina.cateringmanager.model.*;
import pl.karolina.cateringmanager.repository.ClientRepository;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;

import java.time.LocalDate;
import java.util.List;

public class Main {
    static void main(String[] args) {
        System.out.println("Catering manager");

        ClientRepository cr = new ClientRepository();
        Client kowalski = cr.findById(1).orElse(null);
        OrderRepository or = new OrderRepository();
        Order order = new Order(1, kowalski, LocalDate.now(), Calories.KCAL_1200, DietType.STANDARD, 0.0, 53);
        or.save(order);
        List<Order> orderByClientId = or.findOrdersByClientId(1);
        orderByClientId.forEach(System.out::println);

        PriceRepository pr = new PriceRepository();
        List<Price> prices = pr.findAll();
        prices.forEach(System.out::println);


    }
}
