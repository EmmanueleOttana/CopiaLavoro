package it.storeottana.vendita_prodotti.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @ElementCollection
    @Column(nullable = false)
    private List<String> fileNames;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private double price;

    public Product(String name, List<String> fileNames, String title,
                   String description, double price) {
        this.name = name;
        this.fileNames = fileNames;
        this.title = title;
        this.description = description;
        this.price = price;
    }
}
