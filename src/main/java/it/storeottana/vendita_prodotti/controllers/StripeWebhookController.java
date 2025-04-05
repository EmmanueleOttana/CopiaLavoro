package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.repositories.OrderRepo;
import it.storeottana.vendita_prodotti.servicies.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartRepo cartRepo;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {
        return orderService.checkout(request);
    }
}
