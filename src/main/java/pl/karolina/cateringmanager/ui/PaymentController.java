package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.service.PaymentService;

public class PaymentController {

    private final PaymentService ps;
    private final Printer printer;
    private final DataReader reader;
    private final Menu menu;

    public PaymentController(PaymentService ps, Printer printer, DataReader reader, Menu menu) {
        this.ps = ps;
        this.printer = printer;
        this.reader = reader;
        this.menu = menu;
    }


}
