package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.service.ClientService;

import java.util.*;

public class ClientController {

    private final ClientService cs;
    private final DataReader reader;
    private final Printer printer;

    public ClientController(ClientService cs, DataReader reader, Printer printer) {
        this.cs = cs;
        this.reader = reader;
        this.printer = printer;
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
        final int MAX_INGREDIENTS = 4;
        Set<String> exclusions = new HashSet<>();
        while (true) {
            String choice = reader.readText("Dodać wykluczenia? t/n").trim();
            if (choice.equalsIgnoreCase("t")) {
                if (exclusions.size() >= MAX_INGREDIENTS) {
                    printer.print("Osiągnięto mksymalną liczbe wykluczeń");
                    return exclusions;
                }
                String ingredient = reader.readText("Wpisz wykluczenie").trim();
                if (exclusions.add(ingredient)) {
                    printer.print("Dodano wykluczenie" + ingredient);
                } else {
                    printer.print("To wykluczenie już istnieje");
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

    private List<Client> searchClientWithRetry() {
        while (true) {
            String query = reader.readText("Wpisz dane klienta do wyszukania").trim();
            List<Client> clients = cs.searchClients(query);
            if (clients.isEmpty()) {
                printer.print("Brak danych w bazie");
                String choice = reader.readText("Chcesz szukac dalej? t/n").trim();
                if (choice.equalsIgnoreCase("t")) {
                    continue;
                } else if (choice.equalsIgnoreCase("n")) {
                    return new ArrayList<>();
                }
            } else {
                return clients;
            }
        }
    }

    public void updateClientData () {
        List<Client> clients = searchClientWithRetry();
        int id = reader.readPositiveNumber("Wpisz id klienta którego chcesz edytować");
        Optional<Client> client = cs.findById(id);
        printDataToChange();
        int toChange = reader.readPositiveNumber("Które dane chcesz zmienić");
        client.ifPresent(c -> {
            switch (toChange) {
                case 1 -> c.setName(reader.readText("Podaj nowe Imię i Nazwisko").trim());
                case 2 -> c.setAddress(reader.readText("Podaj nowy adres").trim());
                case 3 -> c.setCity(reader.readText("Podaj nową miejscowość").trim());
                case 4 -> c.setPhone(reader.readText("Podaj nowy nr telefonu").trim());
                case 5 -> changeExclusion(c);
                default -> printer.print("Niepoprawny wybór");
            }
            cs.updateClient(c);
        });
    }

    private void changeExclusion(Client client) {
        for (String ingredient : client.getExclusions()) {
            printer.print(ingredient);
            int choice = reader.readPositiveNumber("1 - Zmień / 2 - Usuń / 3 - Zostaw");
            switch (choice) {
                case 1 -> {
                    String newIngredient = reader.readText("Wpisz nowe wykluczenie").trim();
                    cs.deleteExclusion(client.getId(), ingredient);
                    cs.addExclusion(client.getId(), newIngredient);
                    printer.print("Zmieniono wykluczenie");
                }
                case 2 -> cs.deleteExclusion(client.getId(), ingredient);
                default -> printer.print("Niepoprawny wybór");
            }
        }
    }

    private void printDataToChange() {
        printer.print("1 - Imię i Nazwisko");
        printer.print("2 - Adres");
        printer.print("3 - Miejscowość");
        printer.print("4 - Nr telefonu");
        printer.print("5 - Wykluczenia");
    }

    public void deleteClient () {
        List<Client> clients = searchClientWithRetry();
        printer.printAll(clients);
        int id = reader.readPositiveNumber("Wpisz id clienta którego chcesz usunąć");
        if (cs.findById(id).isEmpty()) {
            printer.print("Klient o podanym id nie istnieje");
        } else {
            cs.deleteClient(id);
            printer.print("Usunięto klienta o id: " + id );
        }
    }
}
