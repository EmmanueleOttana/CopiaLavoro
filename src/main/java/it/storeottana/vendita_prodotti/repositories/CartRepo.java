package it.storeottana.vendita_prodotti.repositories;

import it.storeottana.vendita_prodotti.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.createdAt < :expirationTime")
    void deleteExpiredCarts(LocalDateTime expirationTime);

    Optional <Cart> findByUsername(String username);
}
