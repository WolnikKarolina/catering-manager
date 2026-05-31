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
        String name = reader.readText("Podaj imię i nazwisko");
        String address = reader.readText("Podaj ulicę i nr ");
        String city = reader.readText("Podaj miasto");
        String phone = reader.readText("Podaj nr telefonu w formacie 123456789");
        Set<String> exclusions = createExclusion();
        return new Client(name, address, city, phone, exclusions);
    }

    private Set<String> createExclusion() {
        final int MAX_INGREDIENTS = 4;
        Set<String> exclusions = new HashSet<>();
        while (true) {
            String choice = reader.readText("Dodać wykluczenia? t/n");
            if (choice.equals("t")) {
                if (exclusions.size() >= MAX_INGREDIENTS) {
                    printer.print("Osiągnięto mksymalną liczbe wykluczeń");
                    return exclusions;
                }
                String ingredient = reader.readText("Wpisz wykluczenie");
                if (exclusions.add(ingredient)) {
                    printer.print("Dodano wykluczenie");
                } else {
                    printer.print("To wykluczenie już istnieje");
                }
            } else if (choice.equals("n")) {
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

    public void findClient() {
        printer.printAll(searchWithRetry());
    }

    private List<Client> searchWithRetry() {
        while (true) {
            String query = reader.readText("Wpisz dane do wyszukania");
            List<Client> clients = cs.searchClients(query);
            if (clients.isEmpty()) {
                printer.print("Brak danych w bazie");
                String choice = reader.readText("Chcesz szukac dalej? t/n");
                if (choice.equals("t")) {
                    continue;
                } else if (choice.equals("n")) {
                    return new ArrayList<>();
                }
            } else {
                return clients;
            }
        }
    }

    public void updateClientData () {
        List<Client> clients = searchWithRetry();
        int id = reader.readPositiveNumber("Wpisz id klienta którego chcesz edytować");
        Optional<Client> client = cs.findById(id);
        printDataToChange();
        int toChange = reader.readPositiveNumber("Które dane chcesz zmienić");
        client.ifPresent(c -> {
            switch (toChange) {
                case 1 -> c.setName(reader.readText("Podaj nowe Imię i Nazwisko"));
                case 2 -> c.setAddress(reader.readText("Podaj nowy adres"));
                case 3 -> c.setCity(reader.readText("Podaj nową miejscowość"));
                case 4 -> c.setPhone(reader.readText("Podaj nowy nr telefonu"));
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
                    String newIngredient = reader.readText("Wpisz nowe wykluczenie");
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
        List<Client> clients = searchWithRetry();
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
