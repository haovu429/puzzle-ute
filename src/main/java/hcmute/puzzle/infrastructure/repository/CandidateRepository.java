package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

  //      @Query(
  //              "SELECT COUNT(p) FROM ProductEntity p WHERE p.productCode = :product_code AND p.id
  //   <> :updateId")
  //    long countByProductCodeWithUpdateId(
  //            @Param("product_code") String product_code, @Param("updateId") long updateId);

}
