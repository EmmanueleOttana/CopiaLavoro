package it.storeottana.vendita_prodotti.servicies;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OrderService {

    public static String generateOrderNumber() {
        return "ORD-" + Instant.now().toEpochMilli();
    }


}
