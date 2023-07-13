package hcmute.puzzle.infrastructure.repository;

import hcmute.puzzle.infrastructure.entities.Package;
import hcmute.puzzle.infrastructure.entities.RightsOfEmployerWithCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RightsOfEmployerWithCompanyRepository extends JpaRepository<RightsOfEmployerWithCompany, Long> {

	List<RightsOfEmployerWithCompany> findAllByEmployerIdAndCompanyId(long employer_id, long company_id);
}
