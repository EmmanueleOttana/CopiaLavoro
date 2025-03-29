package it.storeottana.vendita_prodotti.dto;

import lombok.Data;

@Data
public class AdminRequest {
    private String username;
    private String email;
    private String telephoneNumber;
    private String password;
    private String authCode;
}
