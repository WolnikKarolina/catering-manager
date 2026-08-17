package pl.karolina.cateringmanager.model;

public enum SettlementMode {
    SPECIFIC_PERIOD ("Zakres dat"),
    OLDEST_UNPAID_FIRST("Najstarsze nieopłacone");

    private final String description;

    SettlementMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
