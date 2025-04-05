package it.storeottana.vendita_prodotti.servicies;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.Order;
import it.storeottana.vendita_prodotti.entities.StateOfOrder;
import it.storeottana.vendita_prodotti.repositories.AdminRepo;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private EmailService postman;
    @Value("${stripe.secret.key}")
    private String secretKey;
    @Value("${urlBackend}")
    private String urlBackend;
    @Value("${companyEmail}")
    private String companyEmail;

    public String generateOrderNumber() {
        return "ORD-" + Instant.now().toEpochMilli();
    }


    public Object getOrder(String orderNumber) {
        Optional <Order> OrderR = orderRepo.findByOrderNumber(orderNumber);
        if (OrderR.isEmpty()) return "Numero ordine non trovato!";
        return OrderR.get();
    }
    public Object getAll(HttpServletRequest request) {
        String username = tokenJWT.getUsername(request.getHeader("Token"));
        if (adminRepo.findByUsername(username).isEmpty()) return "Errore richiesta!";
        return orderRepo.findAll();
    }
    public String requestCancellation(String orderNumber) {
        Optional <Order> orderR = orderRepo.findByOrderNumber(orderNumber);
        if (orderR.isEmpty()) return "Ordine non trovato!";

        postman.sendMail(companyEmail,"Richiesta d'annullamento",
                "É stato richiesto l'annullamento dell'ordine numero: "+orderNumber);

        return "Richiesta d'annullamento inoltrata!";
    }

    public Object orderCancellation(@PathVariable String orderNumber, HttpServletRequest request) {
        String username = tokenJWT.getUsername(request.getHeader("Token"));
        if (adminRepo.findByUsername(username).isEmpty()) return "Errore richiesta!";

        Optional <Order> orderR = orderRepo.findByOrderNumber(orderNumber);
        if (orderR.isEmpty()) return "Ordine non trovato!";

        orderR.get().setStateOfOrder(StateOfOrder.CANCELED);
        orderRepo.saveAndFlush(orderR.get());

        postman.sendMail(orderR.get().getShippingData().getEmail(), "Ordine cancellato!",
                """
                        Gentile cliente,
                        
                        La contatto per informarla che il suo ordine è stato cancellato \
                        con successo! A breve riceverà l'accredito dell'importo pagato.
                        
                        Cordiali saluti,
                        Francesco Ricci - store Ottanà""");
        return "Ordine annullato!";
    }
}
