package it.storeottana.vendita_prodotti.entities;

import it.storeottana.vendita_prodotti.dto.ProductAndquantity;
import it.storeottana.vendita_prodotti.dto.ShippingData;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@EqualsAndHashCode
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    @ElementCollection
    private List<ProductAndquantity> productAndquantity;
    private int totalQuantities;
    @Enumerated(EnumType.STRING)
    private DeliveryMethods deliveryMethods; //Metodo di consegna
    private double totalCost;
    @Embedded
    private ShippingData shippingData;
    private LocalDateTime createdAt;
    private String username;
    private String token;

     public Cart(ProductAndquantity productAndquantity, String username, String token) {
         this.productAndquantity = new ArrayList<>(List.of(productAndquantity));
         this.totalQuantities = 1;
         this.deliveryMethods = DeliveryMethods.GRATUITA;
         this.totalCost = productAndquantity.getProduct().getPrice();
         this.createdAt = LocalDateTime.now();
         this.shippingData = new ShippingData();
         this.username = username;
         this.token = token;
    }
    public void addProductsInCart(ProductAndquantity productAndquantity) {
        this.productAndquantity.add(productAndquantity);
    }

    public Cart() {
        this.productAndquantity = new ArrayList<>();
        this.totalQuantities = 1;
        this.deliveryMethods = DeliveryMethods.GRATUITA;
        this.totalCost = 0;
        this.shippingData = new ShippingData();
        this.createdAt = LocalDateTime.now();
        this.username = "";
        this.token = "";
    }

}