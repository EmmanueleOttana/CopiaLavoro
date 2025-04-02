package it.storeottana.vendita_prodotti.entities;

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
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductInCart> productsInCart;
    private int totalQuantities;
    @Enumerated(EnumType.STRING)
    private DeliveryMethods deliveryMethods; //Metodo di consegna
    private double totalCost;
    @Embedded
    private ShippingData shippingData;
    private LocalDateTime createdAt;
    private String username;
    private String token;

     public Cart(ProductInCart productInCart, String username, String token) {
         this.productsInCart = new ArrayList<>(List.of(productInCart));
         this.totalQuantities = 1;
         this.deliveryMethods = DeliveryMethods.GRATUITA;
         this.totalCost = productInCart.getProduct().getPrice();
         this.createdAt = LocalDateTime.now();
         this.username = username;
         this.token = token;
    }
    public void addProductsInCart(ProductInCart productInCart) {
        this.productsInCart.add(productInCart);
    }

    public Cart() {
        this.productsInCart = new ArrayList<>();
        this.totalQuantities = 1;
        this.deliveryMethods = DeliveryMethods.GRATUITA;
        this.totalCost = 0;
        this.shippingData = new ShippingData();
        this.createdAt = LocalDateTime.now();
        this.username = "";
        this.token = "";
    }

}