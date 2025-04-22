package it.storeottana.vendita_prodotti.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int stars;
    @ElementCollection
    private List<String> images;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Feedback(String description, int stars, Product product) {
        this.description = description;
        this.stars = stars;
        this.product = product;
    }

}
