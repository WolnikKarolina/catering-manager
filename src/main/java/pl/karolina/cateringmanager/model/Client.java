package pl.karolina.cateringmanager.model;

import java.util.Objects;
import java.util.Set;

public class Client {
    private int id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private Set<String> exclusions;

    public Client() {
    }

    public Client(String name, String address, String city, String phone, Set<String> exclusions) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.exclusions = exclusions;
    }

    public Client(int id, String name, String address, String city, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
    }

    public Client(int id, String name, String address, String city, String phone, Set<String> exclusions) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.exclusions = exclusions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(Set<String> exclusions) {
        this.exclusions = exclusions;
    }

    @Override
    public String toString() {
        return "Klient nr: " + id +"; " + name + "; " + address + "; " + city + "; tel: " + phone + "; wykluczenia: " + exclusions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
