package pl.karolina.cateringmanager.service;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ClientService {
    private final ClientRepository cr;
    private static final int MAX_INGREDIENTS = 4;

    public ClientService(ClientRepository cr) {
        this.cr = cr;
    }

    public void addClient(Client client) {
        try {
            cr.save(client);
        } catch (Exception e) {
            throw new RuntimeException("Klient z tym numerem telefonu już istnieje", e);
        }
    }

    public List<Client> getAllClients() {
        return cr.findAll();
    }

    public Optional<Client> findById(int id) {
        return cr.findById(id);
    }

    public List<Client> searchClients(String query) {
        return cr.search(query);
    }

    public void addExclusion(int id, String ingredient) {
        cr.addExclusion(id, ingredient);
    }

    public void deleteExclusion(int id, String ingredient) {
        cr.deleteExclusion(id, ingredient);
    }

    public void updateClient(Client client) {
        findById(client.getId()).orElseThrow(() -> new RuntimeException("Brak klienta " + client.getId()));
        cr.update(client);
    }

    public boolean canAddExclusions(Set<String> exclusions) {
        return exclusions.size() >= MAX_INGREDIENTS;
    }

    public boolean addIngredient(Client client, String ingredient) {
        if (client.getExclusions().add(ingredient)) {
            addExclusion(client.getId(), ingredient);
            return true;
        }
        return false;
    }

    public boolean changeExclusion(Client client, String ingredient, String newIngredient) {
        if (client.getExclusions().remove(ingredient) && client.getExclusions().add(newIngredient)) {
            deleteExclusion(client.getId(), ingredient);
            addExclusion(client.getId(), newIngredient);
            return true;
        }
        return false;
    }

    public boolean isDeleteExclusion(Client client, String ingredient) {
        if (client.getExclusions().remove(ingredient)) {
            deleteExclusion(client.getId(), ingredient);
            return true;
        }
        return false;
    }
}
