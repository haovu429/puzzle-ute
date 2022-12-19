package hcmute.puzzle.repository;

import hcmute.puzzle.entities.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    List<InvoiceEntity> findByEmail(String email);
}
