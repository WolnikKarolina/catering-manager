package pl.karolina.cateringmanager.ui;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.service.ClientService;

import java.util.HashSet;
import java.util.Set;

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
        Client client = createClient();
        cs.addClient(client);
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
        Set<String> exclusions = new HashSet<>();
        while (true) {
            String choice = reader.readText("Dodać wykluczenia? t/n");
            if (choice.equals("t")) {
                String ingredient = reader.readText("Wpisz wykluczenie");
                exclusions.add(ingredient);
            } else if (choice.equals("n")) {
                return exclusions;
            } else {
                printer.print("Nieprawidłowy wybór, wpisz t lub n");
            }
        }
    }

}
