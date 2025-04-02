package it.storeottana.vendita_prodotti.repositories;

import it.storeottana.vendita_prodotti.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
