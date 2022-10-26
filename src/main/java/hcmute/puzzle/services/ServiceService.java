package hcmute.puzzle.services;

import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.ServiceDTO;

public interface ServiceService {
    ResponseObject save(ServiceDTO serviceDTO);

    ResponseObject update(ServiceDTO serviceDTO);

    ResponseObject delete(long id);

    ResponseObject getAll();
}
