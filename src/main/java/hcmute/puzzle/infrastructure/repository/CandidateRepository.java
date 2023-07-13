package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

  //      @Query(
  //              "SELECT COUNT(p) FROM ProductEntity p WHERE p.productCode = :product_code AND p.id
  //   <> :updateId")
  //    long countByProductCodeWithUpdateId(
  //            @Param("product_code") String product_code, @Param("updateId") long updateId);
	@Query("SELECT can FROM Candidate can, Experience ex WHERE ex.id = :experienceId AND ex.candidate.id = can.id")
	Optional<Candidate> findByExperienceId(@Param("experienceId")long experienceId);
}
