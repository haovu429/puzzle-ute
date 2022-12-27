package hcmute.puzzle.services;

import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.EmployerEntity;
import hcmute.puzzle.model.payload.request.company.CreateCompanyPayload;
import hcmute.puzzle.response.DataResponse;

public interface CompanyService {

  ResponseObject save(CreateCompanyPayload companyPayload, EmployerEntity createEmployer);

  ResponseObject update(CompanyDTO companyDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getAllCompanyInActive();

  ResponseObject getOneById(long id);

  ResponseObject getCompanyFollowedByCandidateId(long candidateId);

  DataResponse getCreatedCompanyByEmployerId(long employerId);
}
