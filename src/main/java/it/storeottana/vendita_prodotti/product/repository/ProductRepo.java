package it.storeottana.vendita_prodotti.product.repository;

import it.storeottana.vendita_prodotti.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findByTitleContaining(String title);


}
