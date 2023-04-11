package hcmute.puzzle.services;

import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.EmployerEntity;
import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.model.payload.request.company.CreateCompanyPayload;
import hcmute.puzzle.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {

  DataResponse save(CompanyDTO companyPayload, MultipartFile imageFile, EmployerEntity createEmployer) throws NotFoundException;

  DataResponse update(long companyId, CompanyDTO companyPayload, MultipartFile imageFile, EmployerEntity createEmployer);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllCompanyInActive();

  ResponseObject getOneById(long id);

  ResponseObject getCompanyFollowedByCandidateId(long candidateId);

  DataResponse getCreatedCompanyByEmployerId(long employerId);
}
