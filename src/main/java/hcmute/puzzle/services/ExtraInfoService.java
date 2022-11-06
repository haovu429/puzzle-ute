package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResponseObject;

public interface ExtraInfoService<DTO> {

  ResponseObject save(DTO dto);

  ResponseObject update(DTO dto);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getByType(String type);

  ResponseObject getOneById(long id);
}
