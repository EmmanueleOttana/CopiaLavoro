package it.storeottana.vendita_prodotti.product.controller;

import com.stripe.exception.StripeException;
import it.storeottana.vendita_prodotti.product.entity.Product;
import it.storeottana.vendita_prodotti.product.repository.ProductRepo;
import it.storeottana.vendita_prodotti.product.service.ProductService;
import it.storeottana.vendita_prodotti.product.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepo productRepo;

    @PostMapping("/checkout/{productId}")
    public ResponseEntity<String> processPayment(@PathVariable Long productId,
                                                 @RequestBody Map<String, String> payload) {
        String token = payload.get("token");

        Optional<Product> product = productRepo.findById(productId);
        if (product.isPresent()) {
            try {
                String status = paymentService.processPayment(product.get(), token);
                return ResponseEntity.ok(status);
            } catch (StripeException e) {
                return ResponseEntity.badRequest().body("Errore nel pagamento");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

