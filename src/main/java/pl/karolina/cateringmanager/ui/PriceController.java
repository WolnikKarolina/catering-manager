package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.Price;
import pl.karolina.cateringmanager.service.PriceService;

import java.util.Optional;

public class PriceController {

    private final PriceService ps;
    private final DataReader reader;
    private final Printer printer;

    public PriceController(PriceService ps, DataReader reader, Printer printer) {
        this.ps = ps;
        this.reader = reader;
        this.printer = printer;
    }

    public void printAllPrices() {
        printer.print("Obowiązujący cennik:");
        ps.getAll().forEach(printer::print);
    }

    public void updatePrices() {
        printAllPrices();
        int kcal = reader.readPositiveNumber("Wpisz kaloryczność, której cenę chcesz edytować");
        Optional<Price> optionalPrice = ps.findByCalories(kcal);
        if (optionalPrice.isEmpty()) {
            printer.print("Brak ceny dla podanej kaloryczności");
            return;
        }
        Price price = optionalPrice.get();
        double newPrice = (double) reader.readPositiveNumber("Wprowadź nową cenę");
        price.setPrice(newPrice);
        ps.update(price);
        printer.print("Zmieniono cenę: " + price);
    }


}
