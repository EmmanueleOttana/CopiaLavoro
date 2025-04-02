package it.storeottana.vendita_prodotti.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "client_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String orderNumber;
    @ManyToOne
    private Cart cart;
    private LocalDateTime timestamp;

    public Order(String orderNumber, Cart cart) {
        this.orderNumber = orderNumber;
        this.cart = cart;
        this.timestamp = LocalDateTime.now();
    }

}
