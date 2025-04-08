package it.storeottana.vendita_prodotti.dto;

import it.storeottana.vendita_prodotti.entities.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class ProductAndquantity {

    @ManyToOne
    private Product product;
    private int quantity;

    public ProductAndquantity(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

}