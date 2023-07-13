package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.entities.Company;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.entities.RightsOfEmployerWithCompany;
import hcmute.puzzle.infrastructure.repository.CompanyRepository;
import hcmute.puzzle.infrastructure.repository.EmployerRepository;
import hcmute.puzzle.infrastructure.repository.RightsOfEmployerWithCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

	@Autowired
	EmployerRepository employerRepository;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	RightsOfEmployerWithCompanyRepository rightsOfEmployerWithCompanyRepository;

	public void addRightsOfEmployerWithCompany(long employerId, long companyId) {
		Employer employer = employerRepository.findById(employerId)
											  .orElseThrow(() -> new NotFoundDataException("Not found employer"));
		Company company = companyRepository.findById(companyId)
										   .orElseThrow(() -> new NotFoundDataException("Not found company"));

		List<RightsOfEmployerWithCompany> rights = rightsOfEmployerWithCompanyRepository.findAllByEmployerIdAndCompanyId(
				employer.getId(), company.getId());
		if (rights.isEmpty()) {
			RightsOfEmployerWithCompany rightsOfEmployerWithCompany = RightsOfEmployerWithCompany.builder()
																								 .employer(employer)
																								 .company(company)
																								 .build();
			rightsOfEmployerWithCompanyRepository.save(rightsOfEmployerWithCompany);
		}


	}

	public void removeRightsOfEmployerWithCompany(long employerId, long companyId) {
		List<RightsOfEmployerWithCompany> rights = rightsOfEmployerWithCompanyRepository.findAllByEmployerIdAndCompanyId(
				employerId, companyId);
		rightsOfEmployerWithCompanyRepository.deleteAll(rights);
	}
}
