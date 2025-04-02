package it.storeottana.vendita_prodotti.repositories;

import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.entities.ProductInCart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInCartRepo extends JpaRepository<ProductInCart, Long> {
    ProductInCart findByCartAndProduct(Cart cart, Product product);
    @Transactional
    void deleteByCartAndProduct(Cart cart, Product product);
}
