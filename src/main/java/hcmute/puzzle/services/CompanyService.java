package hcmute.puzzle.services;

import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.UserDTO;

public interface CompanyService {

    ResponseObject save(CompanyDTO companyDTO);

    ResponseObject update(CompanyDTO companyDTO);

    ResponseObject delete(long id);

    ResponseObject getAll();


    ResponseObject getAllCompanyInActive();

    ResponseObject getOneById(long id);

}
