package pl.karolina.cateringmanager.service;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.model.Order;
import pl.karolina.cateringmanager.model.OrderData;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PriceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public void addOrder(Order order) {
        applyPrice(order);
        or.save(order);
    }

    public Optional<Order> findOrderById(int orderId) {
        return or.findOrderById(orderId);
    }

    public List<Order> findOrdersByDate(int clientId, LocalDate startDate, LocalDate finishDate) {
        return or.findOrdersByClientIdByDates(clientId, startDate, finishDate);
    }

    private void applyPrice(Order order) {
        pr.findByCalories(order.getCalories().getKcal())
                .ifPresent(price -> order.setPrice(price.getPrice()));
    }

    public List<Order> findOrderByClientId(int id) {
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

    public double sumOrdersFromClient(int clientId) {
        return (double) findOrderByClientId(clientId).stream()
                .mapToDouble(Order::getPrice)
                .sum();
    }

    public void markAsPaid(Order order) {
        order.setPaid(true);
        updateOrder(order);
    }

    public List<Order> getUnpaidOrdersSortedByDate(int clientId) {
        List<Order> ordersByClientId = or.findOrdersByClientId(clientId);
        return ordersByClientId.stream()
                .filter(o -> !o.isPaid())
                .sorted(Comparator.comparing(Order::getDate))
                .toList();
    }

    public List<Order> settleOldestFirst(Client client, double amount) {
        List<Order> paidOrders = new ArrayList<>();
        List<Order> unpaidOrders = getUnpaidOrdersSortedByDate(client.getId());
        double remaining = amount;
        remaining = makeOrdersPaid(unpaidOrders, remaining, paidOrders);
        client.setCredit(client.getCredit() + remaining);
        return paidOrders;
    }

    private double makeOrdersPaid (List<Order> unpaidOrders, double remaining, List<Order> paidOrders) {
        for (Order order : unpaidOrders) {
            if (remaining >= order.getPrice()) {
                order.setPaid(true);
                remaining = remaining - order.getPrice();
                or.updatePaidStatus(order);
                paidOrders.add(order);
            } else {
                break;
            }
        }
        return remaining;
    }

    public List<Order> settleByPeriod(Client client, double amount, LocalDate startDate, LocalDate finishDate) {
        List<Order> paidOrders = new ArrayList<>();
        double remaining = amount;
        List<Order> ordersByDate = or.findOrdersByClientIdByDates(client.getId(), startDate, finishDate);
        boolean isAnyonePaid = ordersByDate.stream()
                .anyMatch(Order::isPaid);
        if (isAnyonePaid) {
            client.setCredit(client.getCredit() + remaining);
            return paidOrders;
        }
        remaining = makeOrdersPaid(ordersByDate, remaining, paidOrders);
        client.setCredit(client.getCredit() + remaining);
        return paidOrders;
    }
}
