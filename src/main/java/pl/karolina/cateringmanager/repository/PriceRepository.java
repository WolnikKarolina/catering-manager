package pl.karolina.cateringmanager.repository;

import pl.karolina.cateringmanager.db.DatabaseConnection;
import pl.karolina.cateringmanager.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PriceRepository {

    public List<Price> findAll() {
        List<Price> prices = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM prices")) {
            while (rs.next()) {
                Price price = new Price();
                price.setCalories(Calories.fromKcal(rs.getInt("calories")));
                price.setPrice(rs.getDouble("price"));
                prices.add(price);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prices;
    }

    public Optional<Price> findByCalories(int kcal) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM prices WHERE calories = ?")) {
            stmt.setInt(1, kcal);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Price price = new Price();
                    price.setCalories(Calories.fromKcal(rs.getInt("calories")));
                    price.setPrice(rs.getDouble("price"));
                    return Optional.of(price);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void update(Price price) {
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE prices SET price = ? WHERE calories = ?")) {
            stmt.setDouble(1, price.getPrice());
            stmt.setInt(2, price.getCalories().getKcal());
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}




