package pl.karolina.cateringmanager.service;

import pl.karolina.cateringmanager.model.Client;
import pl.karolina.cateringmanager.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientRepository cr;

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

    public List<Client> getAllClients(){
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
        cr.update(client);
    }

    public void deleteClient(int id) {
        cr.delete(id);
    }
}
