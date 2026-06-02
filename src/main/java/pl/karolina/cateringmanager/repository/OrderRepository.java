package pl.karolina.cateringmanager.repository;

import pl.karolina.cateringmanager.db.DatabaseConnection;
import pl.karolina.cateringmanager.model.Calories;
import pl.karolina.cateringmanager.model.DietType;
import pl.karolina.cateringmanager.model.Order;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public void save(Order order) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO orders (client_id, date, calories, diet_type, discount, price) VALUES (?, ?, ?, ?, ?, ? )")) {
            stmt.setInt(1, order.getClientId());
            stmt.setDate(2, Date.valueOf(order.getDate()));
            stmt.setInt(3, order.getCalories().getKcal());
            stmt.setString(4, order.getDietType().name());
            stmt.setDouble(5, order.getDiscount());
            stmt.setDouble(6, order.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order to database", e);
        }
    }

    public Order findOrderById(int orderId) {
        Order order = new Order();
       try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM orders WHERE id = ?")) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                order.setId(rs.getInt("id"));
                order.setClientId(rs.getInt("client_id"));
                order.setDate(rs.getDate("date").toLocalDate());
                order.setCalories(Calories.fromKcal(rs.getInt("calories")));
                order.setDietType(DietType.valueOf(rs.getString("diet_type")));
                order.setDiscount(rs.getDouble("discount"));
                order.setPrice(rs.getDouble("price"));
                return order;
            }
            throw new RuntimeException("Order not found: " + orderId);
       } catch (SQLException e) {
           throw new RuntimeException("Failed to get order with id: " + orderId, e);
       }
    }

    public List<Order> findOrdersByClientIdByDates(int clientId, LocalDate startDate, LocalDate finishDate) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM orders WHERE client_id = ? AND date BETWEEN ? AND ?")) {
            stmt.setInt(1, clientId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(finishDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setClientId(rs.getInt("client_id"));
                    order.setDate(rs.getDate("date").toLocalDate());
                    order.setCalories(Calories.fromKcal(rs.getInt("calories")));
                    order.setDietType(DietType.valueOf(rs.getString("diet_type")));
                    order.setDiscount(rs.getDouble("discount"));
                    order.setPrice(rs.getDouble("price"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch orders for client id: " + clientId, e);
        }
        return orders;
    }

    public List<Order> findOrdersByClientId(int clientId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM orders WHERE client_id = ?")) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setClientId(rs.getInt("client_id"));
                    order.setDate(rs.getDate("date").toLocalDate());
                    order.setCalories(Calories.fromKcal(rs.getInt("calories")));
                    order.setDietType(DietType.valueOf(rs.getString("diet_type")));
                    order.setDiscount(rs.getDouble("discount"));
                    order.setPrice(rs.getDouble("price"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch orders for client id: " + clientId, e);
        }
        return orders;
    }

    public void update(Order order) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE orders SET client_id = ?, date = ?, calories = ?, diet_type = ?, discount = ?, price = ? WHERE id = ?")) {
            stmt.setInt(1, order.getClientId());
            stmt.setDate(2, Date.valueOf(order.getDate()));
            stmt.setInt(3, order.getCalories().getKcal());
            stmt.setString(4, order.getDietType().name());
            stmt.setDouble(5, order.getDiscount());
            stmt.setDouble(6, order.getPrice());
            stmt.setInt(7, order.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order with id: " + order.getId(), e);
        }
    }

    public void deleteOneOrder(int orderId) {
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM orders WHERE id = ?")){
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete order with id: " + orderId, e);
        }
    }

    public void deleteOrdersByDates (int clientId, LocalDate startDate, LocalDate finishDate) {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM orders WHERE id = ? AND date BETWEEN ? AND ?")){
            stmt.setInt(1, clientId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(finishDate));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete orders", e);
        }
    }






}
