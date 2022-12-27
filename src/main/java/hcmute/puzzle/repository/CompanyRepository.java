package hcmute.puzzle.repository;

import hcmute.puzzle.entities.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
  @Query("SELECT c FROM CompanyEntity c WHERE c.isActive = false")
  Set<CompanyEntity> findCompanyEntitiesByActiveFalse();
  @Query("SELECT c FROM CompanyEntity c WHERE c.createdEmployer.id = :createEmployerId")
  Set<CompanyEntity> findByCreatedEmployer_Id(@Param(value = "createEmployerId") long createEmployerId);
}
