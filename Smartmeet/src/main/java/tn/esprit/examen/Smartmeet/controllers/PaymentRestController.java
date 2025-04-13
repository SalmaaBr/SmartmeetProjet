package tn.esprit.examen.Smartmeet.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.Smartmeet.Services.MaryemJeljli.IPaymentServices;
import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Payment;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("Payment")
@RestController
@Tag(name="hello")

public class PaymentRestController {
    private final IPaymentServices paymentServices;


    @PostMapping("/AddPayment")
    public Payment addPayment(@RequestBody Payment payment) {
        return paymentServices.addPayment(payment);
    }

    @GetMapping("/ReadPaymentByID/{id}")
    public Payment retrievePayment(@PathVariable int id) {
        return paymentServices.retrievePayment(id);
    }

    @GetMapping("/ReadAllPayments")
    public List<Payment> retrieveAllPayments() {
        return paymentServices.retrieveAllPayments();
    }

    @DeleteMapping("/DeletePaymentByID/{id}")
    public void deletePayment(@PathVariable int id) {
        paymentServices.deletePayment(id);
    }

    @PutMapping("/UpdatePaymentByID/id")
    public void updatePayment(@PathVariable int id, @RequestBody Payment payment) {
        paymentServices.updatePayment(id,payment);
    }


}
