package hcmute.puzzle.services;

import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.EmployerEntity;
import hcmute.puzzle.model.payload.request.company.CreateCompanyPayload;
import hcmute.puzzle.response.DataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {

  ResponseObject save(CompanyDTO companyPayload, MultipartFile imageFile, EmployerEntity createEmployer);

  ResponseObject update(CompanyDTO companyDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllCompanyInActive();

  ResponseObject getOneById(long id);

  ResponseObject getCompanyFollowedByCandidateId(long candidateId);

  DataResponse getCreatedCompanyByEmployerId(long employerId);
}
