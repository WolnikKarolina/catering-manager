package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.service.PriceService;

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

}
