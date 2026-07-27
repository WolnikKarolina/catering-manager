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
        int choice = reader.readPositiveNumber( "Wybierz opcję zamówienia:");
        return choice;
    }

    public int getOptionsToEditChoice() {
        printer.print("1 - Kalorie");
        printer.print("2 - Typ diety");
        printer.print("3 - Rabat");
        int choice = reader.readPositiveNumber("Co chcesz edytować?");
        return choice;
    }

    public int getIngredientChoice() {
        printer.print("1 - Zmień");
        printer.print("2 - Usuń");
        printer.print("3 - Zostaw");
        printer.print("4 - Dodaj nowe");
        int choice = reader.readPositiveNumber( "Wybierz opcję");
        return choice;
    }

    public int getEditOrderChoice() {
        printer.print("1 - Edytuj pojedynczy dzień");
        printer.print("2 - Edytuj zamówienia w przedziale czasowym");
        return reader.readPositiveNumber("Wybirz opcję");
    }
}
