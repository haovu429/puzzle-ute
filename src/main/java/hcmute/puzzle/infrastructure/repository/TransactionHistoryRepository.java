package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
}
