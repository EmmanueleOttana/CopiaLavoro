package it.storeottana.vendita_prodotti.servicies;

import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.Order;
import it.storeottana.vendita_prodotti.dto.ProductAndquantity;
import it.storeottana.vendita_prodotti.entities.StateOfOrder;
import it.storeottana.vendita_prodotti.repositories.AdminRepo;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.repositories.OrderRepo;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import it.storeottana.vendita_prodotti.utils.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private AdminService adminService;
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

    public Order getOrder(String orderNumber) throws Exception {
        Optional <Order> OrderR = orderRepo.findByOrderNumber(orderNumber);
        OrderR.orElseThrow(() -> new Exception("Ordine non trovato!"));
        return OrderR.get();
    }
    public List<Order> getAll(HttpServletRequest request) throws Exception {
        String username = tokenJWT.extractUsername(request.getHeader("BearerToken"));
        adminRepo.findByUsername(username).orElseThrow(() -> new Exception("Non autorizzato!"));
        return orderRepo.findAll();
    }
    public String requestCancellation(String orderNumber) throws Exception {
        Optional <Order> orderR = orderRepo.findByOrderNumber(orderNumber);
        orderR.orElseThrow(() -> new Exception("Ordine non trovato!"));

        postman.sendMail(companyEmail,"Richiesta d'annullamento",
                "É stato richiesto l'annullamento dell'ordine numero: "+orderNumber);

        return "Richiesta d'annullamento inoltrata!";
    }
    public String orderCancellation(@PathVariable String orderNumber, HttpServletRequest request) throws Exception {
        String username = tokenJWT.extractUsername(request.getHeader("BearerToken"));
        adminRepo.findByUsername(username).orElseThrow(() -> new Exception("Non autorizzato!"));

        Optional <Order> orderR = orderRepo.findByOrderNumber(orderNumber);
        orderR.orElseThrow(() -> new Exception("Ordine non trovato!"));

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
    @Transactional
    public Order createOrderFromCart(Cart cart) {
        // Crea il nuovo ordine e copia i dati dal carrello
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setOrderPriority(cart.getOrderPriority());
        order.setTotalCost(cart.getTotalCost());
        order.setTotalQuantities(cart.getTotalQuantities());
        order.setShippingData(cart.getShippingData());
        order.setTimestamp(LocalDateTime.now());
        order.setStateOfOrder(StateOfOrder.IN_PREPARATION);

        // Prepara la lista dei ProductInCart per l'ordine
        List<ProductAndquantity> orderProducts = new ArrayList<>();
        for (int i = 0; i < cart.getProductAndquantity().size(); i++) {
            ProductAndquantity pic = new ProductAndquantity();
            pic.setProduct(cart.getProductAndquantity().get(i).getProduct());
            pic.setQuantity(cart.getProductAndquantity().get(i).getQuantity());
            orderProducts.add(pic);
        }
        order.setProductsInCart(orderProducts);

        return order;
    }

    public Order changeOrderState(String orderNumber, StateOfOrder stateOfOrder, HttpServletRequest request) throws Exception {
        Optional <Order> orderR = orderRepo.findByOrderNumber(orderNumber);
        Optional <Admin> adminR = adminService.findAdminByRequest(request);
        orderR.orElseThrow(() -> new Exception("Ordine non trovato!"));
        adminR.orElseThrow(() -> new Exception("Non autorizzato!"));
        orderR.get().setStateOfOrder(stateOfOrder);
        return orderRepo.saveAndFlush(orderR.get());
    }
}
