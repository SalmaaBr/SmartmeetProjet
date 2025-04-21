package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Payment;
import tn.esprit.examen.Smartmeet.repositories.MaryemJeljli.IPaymentRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service

public class PaymentServicesImpl implements IPaymentServices {


    private final IPaymentRepository paymentRepository;

    @Override
    public Payment addPayment(Payment payment) {
        return paymentRepository.save(payment);

    }

    @Override
    public Payment retrievePayment(int id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Payment> retrieveAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public void deletePayment(int id) {
        paymentRepository.deleteById(id);


    }

    @Override
    public void updatePayment(int id, Payment payment) {
        paymentRepository.save(payment);


    }
}
