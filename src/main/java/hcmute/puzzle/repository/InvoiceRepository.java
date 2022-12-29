package hcmute.puzzle.repository;

import hcmute.puzzle.entities.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    List<InvoiceEntity> findByEmail(String email);

    @Query("SELECT SUM(inv.price) FROM InvoiceEntity inv WHERE inv.status=:status")
    long getTotalRevenue(@Param("status") String status);

}
