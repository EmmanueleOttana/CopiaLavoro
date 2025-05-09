package it.storeottana.vendita_prodotti.repositories;

import it.storeottana.vendita_prodotti.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long> {
    Optional <Admin> findByEmail(String email);
    Optional <Admin> findByUsername(String username);
    Optional <Admin> findByTelephoneNumber(String telephoneNumber);
    void deleteByIsActiveFalse();
}
