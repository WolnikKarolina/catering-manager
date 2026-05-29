package pl.karolina.cateringmanager.model;

import java.time.LocalDate;

public class Order {
    private int id;
    private int clientId;
    private LocalDate date;
    private Calories calories;
    private double discount;
    private double price;

    public Order(int id, int clientId, LocalDate date, Calories calories, double discount, double price) {
        this.id = id;
        this.clientId = clientId;
        this.date = date;
        this.calories = calories;
        this.discount = discount;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Calories getCalories() {
        return calories;
    }

    public void setCalories(Calories calories) {
        this.calories = calories;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Zamówienie: " + id + ": Nr klients: " + clientId + ", " + date + "; " + calories + price + "zł";
    }
}
