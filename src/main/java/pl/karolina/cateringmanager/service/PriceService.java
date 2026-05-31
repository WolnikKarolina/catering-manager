package pl.karolina.cateringmanager.service;


import pl.karolina.cateringmanager.model.Price;
import pl.karolina.cateringmanager.repository.PriceRepository;

import java.util.List;
import java.util.Optional;

public class PriceService {

    private final PriceRepository pr;

    public PriceService(PriceRepository pr) {
        this.pr = pr;
    }

    public List<Price> getAll() {
       return pr.findAll();
    }

    public Optional<Price> findByCalories(int kcal) {
        return pr.findByCalories(kcal);
    }

    public void update(Price price) {
        pr.update(price);
    }
}
