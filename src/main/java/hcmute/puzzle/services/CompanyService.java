package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.CompanyDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.EmployerEntity;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.models.payload.request.company.CreateCompanyAdminDto;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {

  DataResponse save(CompanyDto companyPayload, MultipartFile imageFile, EmployerEntity createEmployer) throws NotFoundException;

  DataResponse createCompanyForAdmin(CreateCompanyAdminDto companyAdminDto);

  DataResponse update(long companyId, CompanyDto companyPayload, MultipartFile imageFile, EmployerEntity createEmployer) throws NotFoundException;

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllCompanyInActive();

  ResponseObject getOneById(long id);

  ResponseObject getCompanyFollowedByCandidateId(long candidateId);

  DataResponse getCreatedCompanyByEmployerId(long employerId);
}
