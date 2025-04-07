package it.storeottana.vendita_prodotti.controllers;

import com.stripe.Stripe;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.repositories.OrderRepo;
import it.storeottana.vendita_prodotti.servicies.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;


@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CartRepo cartRepo;

    public StripeWebhookController(@Value("${stripe.secret.key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }
    @PostMapping("/create")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {
        return paymentService.checkout(request);
    }

}