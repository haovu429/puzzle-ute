package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CompanyRepository extends JpaRepository<Company, Long> {
  @Query("SELECT c FROM Company c WHERE c.isActive = false")
  Set<Company> findCompanyEntitiesByActiveFalse();
  @Query("SELECT c FROM Company c WHERE c.createdEmployer.id = :createEmployerId")
  Set<Company> findByCreatedEmployer_Id(@Param(value = "createEmployerId") long createEmployerId);

  List<Company> findCompaniesByName(String name);
}
