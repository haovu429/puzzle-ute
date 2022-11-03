package hcmute.puzzle.services;

import hcmute.puzzle.dto.CompanyDTO;
import hcmute.puzzle.dto.ResponseObject;

public interface ExtraInfoService<DTO> {

    ResponseObject save(DTO dto);

    ResponseObject update(DTO dto);

    ResponseObject delete(long id);

    ResponseObject getAll();

    ResponseObject getOneById(long id);
}
