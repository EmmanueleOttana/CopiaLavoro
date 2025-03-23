package it.storeottana.vendita_prodotti.product.controller;

import com.stripe.exception.StripeException;
import it.storeottana.vendita_prodotti.product.entity.Product;
import it.storeottana.vendita_prodotti.product.repository.ProductRepo;
import it.storeottana.vendita_prodotti.product.service.ProductService;
import it.storeottana.vendita_prodotti.product.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepo productRepo;

    @PostMapping("/checkout/{productId}")
    public ResponseEntity<String> processPayment(@PathVariable Long productId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        String urlImage = "https://res.cloudinary.com/dzaopwmcj/image/upload/v1742655982/storeOttana/";

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            try {
                // Creazione dei parametri per la sessione di checkout
                SessionCreateParams params = SessionCreateParams.builder()
                        // Metodo di pagamento per Stripe (carta)
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PAYPAL)
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.LINK)
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.KLARNA)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        // URL di redirezione al termine del checkout
                        .setSuccessUrl("https://storeottana.it")
                        .setCancelUrl("https://storeottana.it")
                        // Abilita l'inserimento del codice promozionale
                        .setAllowPromotionCodes(true)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        // Quantità iniziale (che potrà essere modificata dall'utente)
                                        .setQuantity(1L)
                                        // Abilita la modifica della quantità nel checkout
                                        .setAdjustableQuantity(
                                                SessionCreateParams.LineItem.AdjustableQuantity.builder()
                                                        .setEnabled(true)
                                                        .setMinimum(1L)
                                                        .build()
                                        )
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("eur")
                                                        // Il prezzo in centesimi
                                                        .setUnitAmount((long) (product.getPrice() * 100))
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        // Nome del prodotto
                                                                        .setName(product.getName())
                                                                        // Titolo del prodotto (descrizione aggiuntiva)
                                                                        .setDescription(product.getTitle())
                                                                        // Aggiunge la prima immagine del prodotto
                                                                        .addImage(urlImage + product.getFileNames().get(0))
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

                // Creazione della sessione di checkout su Stripe
                Session session = Session.create(params);

                // Restituisci l'URL della sessione al client
                return ResponseEntity.ok(session.getUrl());
            } catch (StripeException e) {
                return ResponseEntity.badRequest().body("Errore nella creazione della sessione di pagamento: " + e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

