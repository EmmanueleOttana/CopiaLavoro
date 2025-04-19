package it.storeottana.vendita_prodotti.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @ElementCollection
    @Column(nullable = false)
    private List<String> fileNames;
    @Column(nullable = false, length = 350)
    private String title;
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private double price;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List <Feedback> feedback;

    public Product(String name, List<String> fileNames, String title,
                   String description, double price) {
        this.name = name;
        this.fileNames = fileNames;
        this.title = title;
        this.description = description;
        this.price = price;
    }
    public void addFeedback(Feedback feedback) {
        this.feedback.add(feedback);
    }

}