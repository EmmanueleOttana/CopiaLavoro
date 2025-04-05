package it.storeottana.vendita_prodotti.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ShippingData {
    private String name;
    private String surname;
    private String address;
    private String civicNumber;
    private String city;
    private String province;
    private int zipcode;
    private String email;
    private String telephoneNumber;
}