package hcmute.puzzle.services;

import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {

	DataResponse save(CompanyDto CompanyDto) throws NotFoundException;

	//  DataResponse createCompanyForAdmin(CreateCompanyAdminRequest companyAdminDto);

	DataResponse update(long companyId, CompanyDto companyPayload, MultipartFile imageFile,
			Employer createEmployer) throws NotFoundException;

	ResponseObject delete(long id);

	ResponseObject getAll();

	ResponseObject getAllCompanyInActive();

	ResponseObject getOneById(long id);

	ResponseObject getCompanyFollowedByCandidateId(long candidateId);

	DataResponse getCreatedCompanyByEmployerId(long employerId);
}
