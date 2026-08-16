package pl.karolina.cateringmanager.model;

public enum PaymentMethod {
    CASH("Gotówka"),
    TRANSFER("Przelew");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
