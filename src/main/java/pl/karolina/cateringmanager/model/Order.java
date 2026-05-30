package pl.karolina.cateringmanager.model;

import java.sql.Date;
import java.time.LocalDate;

public class Order {
    private int id;
    private int clientId;
    private Client client;
    private LocalDate date;
    private Calories calories;
    private DietType dietType;
    private double discount;
    private double price;

    public Order() {
    }

    public Order(int clientId, Client client, LocalDate date, Calories calories, DietType dietType, double discount, double price) {
        this.clientId = clientId;
        this.client = client;
        this.date = date;
        this.calories = calories;
        this.dietType = dietType;
        this.discount = discount;
        this.price = price;
    }

    public Order(int id, int clientId, Client client, LocalDate date, Calories calories, DietType dietType, double discount, double price) {
        this.id = id;
        this.clientId = clientId;
        this.client = client;
        this.date = date;
        this.calories = calories;
        this.dietType = dietType;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public DietType getDietType() {
        return dietType;
    }

    public void setDietType(DietType dietType) {
        this.dietType = dietType;
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
        return "Zamówienie: " + id + ": Nr klienta: " + clientId + ", " + date + "; " + calories + " " + dietType + ";" + price + "zł";
    }
}
