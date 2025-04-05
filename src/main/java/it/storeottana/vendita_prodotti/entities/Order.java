package it.storeottana.vendita_prodotti.entities;

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
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductInCart> productsInCart;
    @Column(nullable = false)
    private int totalQuantities;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryMethods deliveryMethods; //Metodo di consegna
    @Column(nullable = false)
    private double totalCost;
    @Embedded
    private ShippingData shippingData;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StateOfOrder stateOfOrder;

    public Order(String orderNumber, List<ProductInCart> productsInCart, int totalQuantities,
                 DeliveryMethods deliveryMethods, double totalCost, ShippingData shippingData) {
        this.orderNumber = orderNumber;
        this.productsInCart = productsInCart;
        this.totalQuantities = totalQuantities;
        this.deliveryMethods = deliveryMethods;
        this.totalCost = totalCost;
        this.shippingData = shippingData;
        this.timestamp = LocalDateTime.now();
        this.stateOfOrder = StateOfOrder.IN_PREPARATION;
    }
}