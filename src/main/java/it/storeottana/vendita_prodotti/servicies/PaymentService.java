package it.storeottana.vendita_prodotti.servicies;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.entities.ProductInCart;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private CartRepo cartRepo;

    public PaymentService(@Value("${stripe.secret.key}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }

    public String processPayment(Product product, String token) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (product.getPrice() * 100)); // Converti in centesimi
        chargeParams.put("currency", "eur");
        chargeParams.put("source", token);
        chargeParams.put("description", "Acquisto di " + product.getName());

        Charge charge = Charge.create(chargeParams);
        return charge.getStatus();
    }

    public ResponseEntity<String> processPayment(HttpServletRequest request) {
        // Estrazione del token dai cookie
        String token = tokenJWT.getTokenFromCookie(request);

        if (token == null) {
            return ResponseEntity.badRequest().body("Token del carrello non trovato nei cookie.");
        }

        // Recupero del carrello tramite il token
        Optional<Cart> cart = cartRepo.findByUsername(tokenJWT.getUsername(token));
        if (cart.isEmpty() || cart.get().getProductsInCart().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String urlImage = "https://res.cloudinary.com/dzaopwmcj/image/upload/v1742655982/storeOttana/";

        try {
            // Costruzione dei parametri per la sessione di checkout
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PAYPAL)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.LINK)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.KLARNA)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://storeottana.it")
                    .setCancelUrl("https://storeottana.it")
                    .setAllowPromotionCodes(true)
                    // Aggiunge il riferimento al carrello
                    .setClientReferenceId(String.valueOf(cart.get().getId()));

            // Ciclo per aggiungere ogni prodotto presente nel carrello come line item
            for (ProductInCart pc : cart.get().getProductsInCart()) {
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity((long) pc.getQuantity())
                        .setAdjustableQuantity(
                                SessionCreateParams.LineItem.AdjustableQuantity.builder()
                                        .setEnabled(true)
                                        .setMinimum(1L)
                                        .build()
                        )
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("eur")
                                        .setUnitAmount((long) (pc.getProduct().getPrice() * 100))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(pc.getProduct().getName())
                                                        .setDescription(pc.getProduct().getTitle())
                                                        .addImage(urlImage + pc.getProduct().getFileNames().get(0))
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();
                paramsBuilder.addLineItem(lineItem);
            }

            // Creazione della sessione di checkout su Stripe
            SessionCreateParams params = paramsBuilder.build();
            Session session = Session.create(params);

            // Restituzione dell'URL della sessione al client
            return ResponseEntity.ok(session.getUrl());
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Errore nella creazione della sessione di pagamento: " + e.getMessage());
        }
    }
}

