package pl.karolina.cateringmanager.ui;

import java.util.Scanner;

public class DataReader {

    private final Printer printer;
    private final Scanner sc = new Scanner(System.in);

    public DataReader(Printer printer) {
        this.printer = printer;
    }

    public String readText(String prompt) {
        printer.print(prompt);
        String text;
        do {
            text = sc.nextLine().trim();
            if (text.isEmpty()) {
                printer.print("Pole nie może byc puste");
            }
        } while (text.isEmpty());
        return text;
    }




}
