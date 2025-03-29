package it.storeottana.vendita_prodotti.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String telephoneNumber;
    @Column(nullable = false)
    private String password;
    private String activationCode;
    private boolean isSuspended;
    private boolean isActive;
    private String token;
    private LocalDateTime timestampToken;

    public Admin(String username, String email, String telephoneNumber,
                 String password) {
        this.username = username;
        this.email = email;
        this.telephoneNumber = telephoneNumber;
        this.password = password;
        this.activationCode = UUID.randomUUID().toString();
        this.isActive = false;
        this.isSuspended = true;
    }
}