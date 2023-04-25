package hcmute.puzzle.services;

import hcmute.puzzle.dto.ExtraInfoDTO;
import hcmute.puzzle.dto.ResponseObject;

public interface ExtraInfoService {

  ResponseObject save(ExtraInfoDTO dto);

  ResponseObject update(ExtraInfoDTO dto, long id);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getByType(String type);

  ResponseObject getOneById(long id);
}
