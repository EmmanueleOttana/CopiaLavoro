package it.storeottana.vendita_prodotti.repositories;

import it.storeottana.vendita_prodotti.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
}
