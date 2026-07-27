package pl.karolina.cateringmanager.model;

public record OrderData(Calories calories, DietType dietType, Double discount, Price price) {
}
