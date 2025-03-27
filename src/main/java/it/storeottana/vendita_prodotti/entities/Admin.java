package it.storeottana.vendita_prodotti.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String email;
    private String telephoneNumber;
    private String password;
    private String activationCode;
    private boolean isSuspended;
    //private boolean isActive;
    private String token;
    private LocalDateTime timestampToken;

    public Admin(String username, String email, String telephoneNumber,
                 String password) {
        this.username = username;
        this.email = email;
        this.telephoneNumber = telephoneNumber;
        this.password = password;
        this.activationCode = UUID.randomUUID().toString();
        this.isSuspended = true;
    }
}