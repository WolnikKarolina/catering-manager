package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.service.ClientService;

import java.util.*;

public class ClientController {

    private final ClientService cs;
    private final DataReader reader;
    private final Printer printer;
    private final Menu menu;

    public ClientController(ClientService cs, DataReader reader, Printer printer, Menu menu) {
        this.cs = cs;
        this.reader = reader;
        this.printer = printer;
        this.menu = menu;
    }

    public void addClient() {
        printer.print("Dodawanie nowego klienta");
        Client client = createClient();
        cs.addClient(client);
        printer.print("Dodano klienta: " + client);
    }

    private Client createClient() {
        String name = reader.readText("Podaj imię i nazwisko").trim();
        String address = reader.readText("Podaj ulicę i nr ").trim();
        String city = reader.readText("Podaj miasto").trim();
        String phone = reader.readText("Podaj nr telefonu w formacie 123456789").trim();
        Set<String> exclusions = createExclusion();
        return new Client(name, address, city, phone, exclusions);
    }

    private Set<String> createExclusion() {
        Set<String> exclusions = new HashSet<>();
        while (true) {
            if (cs.canAddExclusions(exclusions)) return exclusions;
            String choice = reader.readText("Dodać wykluczenia? t/n").trim();
            if (choice.equalsIgnoreCase("t")) {
                String ingredient = reader.readText("Wpisz wykluczenie").trim();
                if (exclusions.add(ingredient)) {
                    printer.print("Dodano wykluczenie: " + ingredient);
                    if (cs.canAddExclusions(exclusions)) return exclusions;
                } else {
                    printer.print(" -> To wykluczenie już istnieje <-");
                }
            } else if (choice.equalsIgnoreCase("n")) {
                return exclusions;
            } else {
                printer.print("Nieprawidłowy wybór, wpisz t lub n");
            }
        }
    }

    public void printAllClients() {
        printer.print("--- Wszyscy klienci ---");
        List<Client> clients = cs.getAllClients();
        if (clients.isEmpty()) {
            printer.print("Brak klientów w bazie");
        } else {
            printer.printAll(clients);
        }
    }

    public Optional<Client> findClientById(int id) {
        Optional<Client> client = cs.findById(id);
        if (client.isEmpty()) {
            printer.print("Brak klienta o podanym id");
        }
        return client;
    }

    public void printClient() {
        printer.printAll(searchClientWithRetry());
    }

    public List<Client> searchClientWithRetry() {
        while (true) {
            String query = reader.readText("Wpisz dane klienta do wyszukania").trim();
            List<Client> clients = cs.searchClients(query);
            if (clients.isEmpty()) {
                printer.print("Brak danych w bazie");
                String choice = reader.readText("Chcesz szukac dalej? t/n").trim();
                if (choice.equalsIgnoreCase("n")) {
                    return new ArrayList<>();
                }
            } else {
                return clients;
            }
        }
    }

    public void updateClientData() {
        List<Client> clients = searchClientWithRetry();
        printer.printAll(clients);
        int id = reader.readPositiveNumber("Wpisz id klienta którego chcesz edytować");
        Optional<Client> client = cs.findById(id);
        if (client.isEmpty()) {
            printer.print("Klient nie istnieje");
            return;
        }
        Client c = client.get();
        menu.printDataToChange();
        while (true) {
            int toChange = reader.readPositiveNumber("Które dane chcesz zmienić");
            if (applyChange(toChange, c)) return;
            String choice = reader.readText("Czy chcesz zmienić jeszcze jakieś dane? t/n");
            if (choice.equalsIgnoreCase("n")) {
                return;
            }
        }
    }

    private boolean applyChange(int toChange, Client c) {
        switch (toChange) {
            case 1 -> {
                c.setName(reader.readText("Podaj nowe Imię i Nazwisko").trim());
                cs.updateClient(c);
            }
            case 2 -> {
                c.setAddress(reader.readText("Podaj nowy adres").trim());
                cs.updateClient(c);
            }
            case 3 -> {
                c.setCity(reader.readText("Podaj nową miejscowość").trim());
                cs.updateClient(c);
            }
            case 4 -> {
                c.setPhone(reader.readText("Podaj nowy nr telefonu").trim());
                cs.updateClient(c);
            }
            case 5 -> {
                changeExclusion(c, menu);
            }
            case 6 -> {
                return true;
            }
            default -> printer.print("Niepoprawny wybór");
        }
        return false;
    }

    private void changeExclusion(Client client, Menu menu) {
        if (client.getExclusions().isEmpty()) {
            printer.print("Klient nie ma wykluczeń");
            Set<String> exclusions = createExclusion();
            for (String exclusion : exclusions) {
                cs.addExclusion(client.getId(), exclusion);
            }
            return;
        }
        Set<String> exclusionsCopy = new HashSet<>(client.getExclusions());
        for (String ingredient : exclusionsCopy) {
            printer.print(ingredient);
            int choice = menu.getIngredientChoice();
            switch (choice) {
                case 1 -> changeIngredient(client, ingredient);
                case 2 -> deleteIngredient(client, ingredient);
                case 3 -> {
                }
                case 4 -> addIngredient(client);
                default -> printer.print("Niepoprawny wybór");
            }
        }
    }

    private void deleteIngredient(Client client, String ingredient) {
        if (cs.isDeleteExclusion(client, ingredient)) {
            printer.print("Usunięto wykluczenie: " + ingredient);
        } else {
            printer.print("Nie znaleziono takiego wykluczenia");
        }
    }

    private void addIngredient(Client client) {
        String newIngredient = reader.readText("Wpisz nowe wykluczenie").trim();
        if (cs.addIngredient(client, newIngredient)) {
            printer.print("Dodano wykluczenie: " + newIngredient);
        } else {
            printer.print("To wykluczenie już istnieje");
        }
    }

    private void changeIngredient(Client client, String ingredient) {
        String newIngredient = reader.readText("Wpisz nowe wykluczenie").trim();
        if (cs.changeExclusion(client, ingredient, newIngredient)) {
            printer.print("Zmieniono wykluczenie");
        } else {
            printer.print("Nie udało się zmienić wykluczenia");
        }
    }

}
