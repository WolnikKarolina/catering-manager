package pl.karolina.cateringmanager.service;

import pl.karolina.cateringmanager.model.Order;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;

import java.time.LocalDate;
import java.util.List;

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

    public Order findOrderById(int orderId) {
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

    public void updateOrder(Order order) {
        applyPrice(order);
        or.update(order);
    }

    public void deleteOrder(int id) {
        or.deleteOneOrder(id);
    }

    public boolean orderExistForDate(int clientId, LocalDate date) {
        return or.findOrdersByClientId(clientId).stream().anyMatch(o -> o.getDate().equals(date));
    }

}
