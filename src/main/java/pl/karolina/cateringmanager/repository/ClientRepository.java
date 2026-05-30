package pl.karolina.cateringmanager.repository;

import pl.karolina.cateringmanager.db.DatabaseConnection;
import pl.karolina.cateringmanager.model.Client;

import java.sql.*;
import java.util.*;

public class ClientRepository {

    public void save(Client client) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO clients (name, address, city, phone) VALUES (?, ?, ?, ? )")) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getAddress());
            stmt.setString(3, client.getCity());
            stmt.setString(4, client.getPhone());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setName(rs.getString("name"));
                client.setAddress(rs.getString("address"));
                client.setCity(rs.getString("city"));
                client.setPhone(rs.getString("phone"));
                client.setExclusions(findExclusionsByClient(client.getId()));
                clients.add(client);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clients;
    }

    private Set<String> findExclusionsByClient(int id) {
        Set<String> exclusion = new HashSet<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ingredient FROM exclusions WHERE client_id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    exclusion.add(rs.getString("ingredient"));
                }
            }
        } catch ( SQLException e) {
            throw new RuntimeException(e);
        }
        return exclusion;
    }

    public Optional<Client> findById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM clients WHERE id = ?")) {
             stmt.setInt(1, id);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     Client client = new Client();
                     client.setId(rs.getInt("id"));
                     client.setName(rs.getString("name"));
                     client.setAddress(rs.getString("address"));
                     client.setCity(rs.getString("city"));
                     client.setPhone(rs.getString("phone"));
                     client.setExclusions(findExclusionsByClient(client.getId()));
                     return Optional.of(client);
                 }
             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Client> search(String query) {
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM clients WHERE name LIKE ? OR phone LIKE ? OR address LIKE ?")) {
            String pattern = "%" + query + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setName(rs.getString("name"));
                    client.setAddress(rs.getString("address"));
                    client.setCity(rs.getString("city"));
                    client.setPhone(rs.getString("phone"));
                    client.setExclusions(findExclusionsByClient(client.getId()));
                    clients.add(client);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clients;
    }

    public void update(Client client) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE clients SET name = ?, address = ?, city = ?, phone = ? WHERE id = ?")) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getAddress());
            stmt.setString(3, client.getCity());
            stmt.setString(4, client.getPhone());
            stmt.setInt(5, client.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM clients WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
