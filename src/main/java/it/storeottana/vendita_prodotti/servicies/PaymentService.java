package it.storeottana.vendita_prodotti.servicies;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.Order;
import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.entities.ProductInCart;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.repositories.OrderRepo;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import it.storeottana.vendita_prodotti.utils.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private EmailService postman;
    @Autowired
    private OrderService orderService;
    @Value("${stripe.secret.authKey}")
    private String authKey;
    @Value("${urlBackend}")
    private String urlBackend;
    @Autowired
    private OrderRepo orderRepo;

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

    public ResponseEntity<String> checkout(HttpServletRequest request) {
        String payload;
        try (BufferedReader reader = request.getReader()) {
            payload = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nella lettura del payload.");
        }

        String sigHeader = logRequestHeaders(request);
        Event event;
        try {
            assert sigHeader != null;
            event = Webhook.constructEvent(payload, sigHeader, authKey);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore di verifica della firma: " + e.getMessage());
        }

        if ("checkout.session.completed".equals(event.getType())) {
            // Recupera l'oggetto Session dall'evento
            Session session = (Session) event.getData().getObject();
            // Recupera l'ID del carrello salvato in clientReferenceId
            String cartIdStr = session.getClientReferenceId();
            if (cartIdStr != null) {
                try {
                    Long cartId = Long.parseLong(cartIdStr);
                    Optional<Cart> cartOpt = cartRepo.findById(cartId);

                    if (cartOpt.isPresent()) {
                        Cart cart = cartOpt.get();
                        // Crea un nuovo ordine usando il carrello e il numero d'ordine generato
                        Order order = orderService.createOrderFromCart(cart);

                        postman.sendMail(order.getShippingData().getEmail(),"Ordine acquisito correttamente!",
                                "Per visualizzare lo stato del suo ordine, cliccare nel link sottostante:\n" +
                                        urlBackend+"/order/get/"+order.getOrderNumber());

                        cartRepo.deleteById(cart.getId());
                        orderRepo.saveAndFlush(order);
                    }
                } catch (NumberFormatException ex) {
                    // Gestione del caso in cui l'ID del carrello non sia nel formato atteso
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Formato dell'ID del carrello non valido.");
                }
            }
        }
        // Risposta per confermare la ricezione dell'evento
        return ResponseEntity.ok("Ordine effettuato!\n" +
                "Per visualizzare l'ordine cliccare nel link ricevuto per email");
    }
    private String logRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                if (headerName.equals("stripe-signature")){
                    return headerValue;
                }
            }
        }
        return null;
    }

}

