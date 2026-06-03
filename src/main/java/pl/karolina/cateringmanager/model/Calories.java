package pl.karolina.cateringmanager.model;

import java.util.List;

public enum Calories {
    KCAL_1200(1200),
    KCAL_1500(1500),
    KCAL_1800(1800),
    KCAL_2000(2000),
    KCAL_2500(2500),
    KCAL_3000(3000),
    KCAL_3500(3500);

    private final int kcal;

    Calories(int kcal) {
        this.kcal = kcal;
    }

    public int getKcal() {
        return kcal;
    }

    public static Calories fromKcal(int kcal) throws IllegalArgumentException {
        for (Calories value : values()) {
            if ( value.kcal == kcal) {
                return value;
            }
        }
        throw new IllegalArgumentException("Niepoprawna wartość kcal: " + kcal);
    }

    @Override
    public String toString() {
        return String.valueOf(kcal);
    }

    public static List<Calories> getAll() {
        return List.of(values());
    }
}
