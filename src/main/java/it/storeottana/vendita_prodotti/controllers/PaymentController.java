package it.storeottana.vendita_prodotti.controllers;


import it.storeottana.vendita_prodotti.servicies.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<String> processPayment(HttpServletRequest request) {
        return paymentService.processPayment(request);
    }


}

