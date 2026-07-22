package pl.karolina.cateringmanager.repository;


import pl.karolina.cateringmanager.db.DatabaseConnection;
import pl.karolina.cateringmanager.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRepository {

    public void save(Payment payment) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO payments (client_id, date, amount) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getClientId());
            stmt.setDate(2, Date.valueOf(payment.getPaymentDate()));
            stmt.setDouble(3, payment.getAmount());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                payment.setId(keys.getInt(1));
            }
        }catch (SQLException e) {
            throw new RuntimeException("Failed to save payment to database", e);
        }
    }

    public Optional<Payment> findPaymentById (int paymentId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM payments WHERE id = ?")) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Payment payment = mapPayment(rs);
                return Optional.of(payment);
            }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get payment with id " + paymentId, e);
        }
        return Optional.empty();
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setClientId(rs.getInt("client_id"));
        payment.setPaymentDate(rs.getDate("date").toLocalDate());
        payment.setAmount(rs.getDouble("amount"));
        return payment;
    }

    public List<Payment> findPaymentsByClientId(int clientId) {
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM payments WHERE client_id = ?")) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = mapPayment(rs);
                    payments.add(payment);
                }
            }
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get payment with client id " + clientId, e);
        }
    }

    public void deletePayment(int id) {
        try (Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(
                "DELETE FROM payments WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete payments with id " + id, e);
        }
    }

    public double getTotalPaymentsByClient(int clientId) {
        double totalPayments = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT SUM(amount) FROM payments WHERE client_id = ?")) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalPayments = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return totalPayments;
    }




}
