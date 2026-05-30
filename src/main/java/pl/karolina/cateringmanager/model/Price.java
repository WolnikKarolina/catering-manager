package pl.karolina.cateringmanager.model;

public class Price {
    private Calories calories;
    private double price;

    public Price() {
    }

    public Price(Calories calories, double price) {
        this.calories = calories;
        this.price = price;
    }

    public Calories getCalories() {
        return calories;
    }

    public void setCalories(Calories calories) {
        this.calories = calories;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return calories + " cena: " + price + "zł";
    }
}
