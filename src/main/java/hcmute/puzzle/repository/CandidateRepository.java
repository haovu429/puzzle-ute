package hcmute.puzzle.repository;

import hcmute.puzzle.entities.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<CandidateEntity, Long> {

  //      @Query(
  //              "SELECT COUNT(p) FROM ProductEntity p WHERE p.productCode = :product_code AND p.id
  //   <> :updateId")
  //    long countByProductCodeWithUpdateId(
  //            @Param("product_code") String product_code, @Param("updateId") long updateId);

}
