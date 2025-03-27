package it.storeottana.vendita_prodotti.repositories;

import it.storeottana.vendita_prodotti.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoAdmin extends JpaRepository<Admin, Long> {
    Optional <Admin> findByEmail(String email);
}
