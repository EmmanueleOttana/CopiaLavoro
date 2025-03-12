package it.storeottana.vendita_prodotti.product.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import it.storeottana.vendita_prodotti.product.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

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
}

