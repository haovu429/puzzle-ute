package hcmute.puzzle.repository;

import hcmute.puzzle.entities.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
}
