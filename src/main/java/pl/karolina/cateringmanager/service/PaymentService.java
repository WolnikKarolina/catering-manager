package pl.karolina.cateringmanager.service;

import pl.karolina.cateringmanager.model.Payment;
import pl.karolina.cateringmanager.repository.ClientRepository;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;

public class PaymentService {

    private final PaymentRepository pr;

    public PaymentService(PaymentRepository pr) {
        this.pr = pr;
    }

    public void addPayment(Payment payment) {
        if (payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Płatność musi być dodatnia");
        }
        pr.save(payment);
    }

    public Optional<Payment> findPaymentById (int paymentId) {
        return pr.findPaymentById(paymentId);
    }

    public List<Payment> findPaymentByClientId(int clientId) {
        return pr.findPaymentsByClientId(clientId);
    }

    public void deletePayment (int paymentId) {
        findPaymentById(paymentId).orElseThrow(
                () -> new RuntimeException("Payment not found: " + paymentId));
        pr.deletePayment(paymentId);
    }

    public double getTotalPaymentByClient (int clientId) {
        return pr.getTotalPaymentsByClient(clientId);
    }
}
