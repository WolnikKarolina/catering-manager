package pl.karolina.cateringmanager.model;


import java.time.LocalDate;
import java.util.Objects;

public class Payment {
    private int id;
    private int clientId;
    private LocalDate paymentDate;
    private double amount;
    private PaymentMethod paymentMethod;

    public Payment() {
    }

    public Payment( double amount, LocalDate paymentDate, int clientId, PaymentMethod paymentMethod) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.clientId = clientId;
        this.paymentMethod = paymentMethod;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
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
