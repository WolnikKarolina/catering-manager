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

    public int readPositiveNumber(String prompt) {
        printer.print(prompt);
        int choice;
        do {
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
                if (choice <= 0) {
                    printer.print("Podaj liczbę dodatnią");
                }
            } catch (NumberFormatException e) {
                printer.print("Podaj poprawną liczbę");
                choice = 0;
            }
        } while (choice <= 0);
        return choice;
    }




}
