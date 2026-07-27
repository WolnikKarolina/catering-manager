package pl.karolina.cateringmanager.service;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.model.Order;
import pl.karolina.cateringmanager.model.OrderData;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderRepository or;
    private final PriceRepository pr;

    public OrderService(OrderRepository or, PriceRepository pr) {
        this.or = or;
        this.pr = pr;
    }

    public void addOrder(Order order){
        applyPrice(order);
        or.save(order);
    }

    public Optional<Order> findOrderById(int orderId) {
        return or.findOrderById(orderId);
    }

    public List<Order> findOrdersByDate (int clientId, LocalDate startDate, LocalDate finishDate) {
        return or.findOrdersByClientIdByDates(clientId, startDate, finishDate);
    }

    private void applyPrice(Order order) {
        pr.findByCalories(order.getCalories().getKcal())
                .ifPresent(price -> order.setPrice(price.getPrice()));
    }

    public List<Order> findOrderByClientId (int id) {
        return or.findOrdersByClientId(id);
    }

    public List<Order> findOrdersByClientFromDate(Client client, LocalDate date) {
        List<Order> orders = findOrderByClientId(client.getId());
        return orders.stream()
                .filter(o -> !o.getDate().isBefore(date))
                .sorted(Comparator.comparing(Order::getDate))
                .toList();
    }

    public void updateOrder(Order order) {
        findOrderById(order.getId()).orElseThrow(
                () -> new RuntimeException("Brak zamówienia " + order.getId()));
        applyPrice(order);
        or.update(order);
    }

    public void deleteOrder(int orderId) {
        findOrderById(orderId).orElseThrow(
                () -> new RuntimeException("Brak zamówienia " + orderId));
        or.deleteOneOrder(orderId);
    }

    public void deleteOrders(List<Order> ordersByDate) {
        for (Order order : ordersByDate) {
            deleteOrder(order.getId());
        }
    }

    public void createOrders(Client client, List<LocalDate> dates, OrderData orderData) {
         for (LocalDate d : dates) {
            Order order = new Order(client.getId(), client, d, orderData.calories(), orderData.dietType(), orderData.discount(), orderData.price().getPrice());
            addOrder(order);
        }
    }

}
