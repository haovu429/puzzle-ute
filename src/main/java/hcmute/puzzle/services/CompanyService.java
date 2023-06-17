package hcmute.puzzle.services;

import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.request.CreateCompanyAdminRequest;
import hcmute.puzzle.infrastructure.dtos.response.CompanyResponse;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.models.CompanyFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CompanyService {

	CompanyResponse save(CompanyDto CompanyDto) throws NotFoundException;

	//  DataResponse createCompanyForAdmin(CreateCompanyAdminRequest companyAdminDto);

	CompanyResponse updateForAdmin(long companyId, CreateCompanyAdminRequest companyPayload) throws NotFoundException;

	void delete(long id);

	Page<CompanyResponse> getAll(Pageable pageable);

	Page<CompanyResponse> filterCompany(CompanyFilter companyFilter, Pageable pageable);

	CompanyResponse getOneById(long id);

	List<CompanyResponse> getCompanyFollowedByCandidateId(long candidateId);

	List<CompanyResponse> getCreatedCompanyByEmployerId(long employerId);
}
