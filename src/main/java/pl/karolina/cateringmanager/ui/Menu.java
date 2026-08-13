package pl.karolina.cateringmanager.ui;

public class Menu {

    private final Printer printer;
    private final DataReader reader;

    public Menu(Printer printer, DataReader reader) {
        this.printer = printer;
        this.reader = reader;
    }

    public int getProcessOrderChoice() {
        printer.print("1 - Zamównie na pojedyncze dni");
        printer.print("2 - Zamównie na dni robocze");
        printer.print("3 - Zamówienie z sobotami");
        printer.print("4 - Zamówienie razem z weekedami");
        printer.print("5 - Wróć do poprzedniego menu");
        return readValidChoice("Wybierz opcję zamówienia:", 1, 5);
    }

    private int readValidChoice(String prompt, int min, int max) {
        while (true) {
            int choice = reader.readPositiveNumber(prompt);
            if (choice >= min && choice <= max ) {
                return choice;
            } else {
                printer.print("Niepoprawny wybór, spróbuj ponownie");
            }
        }
    }

    public int getOptionsToEditChoice() {
        printer.print("1 - Kalorie");
        printer.print("2 - Typ diety");
        printer.print("3 - Rabat");
        return readValidChoice("Co chcesz edytować?", 1, 3);
    }

    public int getIngredientActionChoice() {
        printer.print("1 - Zmień");
        printer.print("2 - Usuń");
        printer.print("3 - Zostaw");
        printer.print("4 - Dodaj nowe");
        return readValidChoice("Wybierz opcję", 1, 4);
    }

    public int getEditOrderChoice() {
        printer.print("1 - Edytuj pojedynczy dzień");
        printer.print("2 - Edytuj zamówienia w przedziale czasowym");
        return readValidChoice("Wybierz opcję", 1, 2);
    }

    public void printDataToChange() {
        printer.print("1 - Imię i Nazwisko");
        printer.print("2 - Adres");
        printer.print("3 - Miejscowość");
        printer.print("4 - Nr telefonu");
        printer.print("5 - Wykluczenia");
        printer.print("6 - Wróć do poprzedniego menu");
    }
}
