package pl.karolina.cateringmanager.service;

import pl.karolina.cateringmanager.model.Payment;
import pl.karolina.cateringmanager.repository.ClientRepository;
import pl.karolina.cateringmanager.repository.OrderRepository;
import pl.karolina.cateringmanager.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;

public class PaymentService {

    private final PaymentRepository pr;
    private final OrderRepository or;
    private final ClientRepository cr;


    public PaymentService(PaymentRepository pr, OrderRepository or, ClientRepository cr) {
        this.pr = pr;
        this.or = or;
        this.cr = cr;
    }

    public void addPayment(Payment payment) {
        pr.save(payment);
    }

    public Optional<Payment> findPaymentById (int paymentId) {
        return pr.findPaymentById(paymentId);
    }

    public List<Payment> findPaymentByClientId(int clientId) {
        return pr.findPaymentsByClientId(clientId);
    }
}
