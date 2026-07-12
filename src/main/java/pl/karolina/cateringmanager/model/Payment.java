package pl.karolina.cateringmanager.model;


import java.time.LocalDate;
import java.util.Objects;

public class Payment {
    private int id;
    private int clientId;
    private LocalDate paymentDate;
    private double amount;

    public Payment() {
    }

    public Payment(int id, int clientId, LocalDate paymentDate, double amount) {
        this.id = id;
        this.clientId = clientId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public Payment(int clientId, LocalDate paymentDate, double amount) {
        this.clientId = clientId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return clientId == payment.clientId && Double.compare(amount, payment.amount) == 0 && Objects.equals(paymentDate, payment.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, paymentDate, amount);
    }

    @Override
    public String toString() {
        return "Płatność nr: " + id + " z dnia: " + paymentDate + ", kwota: " + amount;
    }
}
