package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;


public interface ExtraInfoService {

  ResponseObject save(ExtraInfoDto dto);

  ResponseObject update(ExtraInfoDto dto, long id);

  ResponseObject delete(long id);

  ResponseObject getAll();

  ResponseObject getByType(String type);

  ResponseObject getOneById(long id);
}
