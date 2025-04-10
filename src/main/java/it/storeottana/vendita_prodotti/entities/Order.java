package it.storeottana.vendita_prodotti.entities;

import it.storeottana.vendita_prodotti.dto.ProductAndquantity;
import it.storeottana.vendita_prodotti.dto.ShippingData;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "client_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String orderNumber;
    @Column(nullable = false)
    @Embedded
    @ElementCollection
    private List<ProductAndquantity> productsInCart;
    @Column(nullable = false)
    private int totalQuantities;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderPriority orderPriority; //Metodo di consegna
    @Column(nullable = false)
    private double totalCost;
    @Embedded
    private ShippingData shippingData;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StateOfOrder stateOfOrder;

    public Order(String orderNumber, List<ProductAndquantity> productsInCart, int totalQuantities,
                 OrderPriority orderPriority, double totalCost, ShippingData shippingData) {
        this.orderNumber = orderNumber;
        this.productsInCart = productsInCart;
        this.totalQuantities = totalQuantities;
        this.orderPriority = orderPriority;
        this.totalCost = totalCost;
        this.shippingData = shippingData;
        this.timestamp = LocalDateTime.now();
        this.stateOfOrder = StateOfOrder.IN_PREPARATION;
    }
}