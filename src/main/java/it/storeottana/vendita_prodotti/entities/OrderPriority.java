package it.storeottana.vendita_prodotti.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderPriority {
    PRIORITARIO("Entro 3 giorni", 20),
    STANDARD("Entro 10 giorni", 0);

    private final String DeliveryTimes;
    private final double costPercentage;

    public double calculateShippingCost(double totalCost) {
        return (totalCost * costPercentage) / 100;
    }

}
