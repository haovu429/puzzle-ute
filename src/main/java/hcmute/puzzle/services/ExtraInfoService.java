package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ExtraInfoService {

  ExtraInfoDto save(ExtraInfoDto dto);

  ExtraInfoDto update(ExtraInfoDto dto, long id);

  void delete(long id);

  Page<ExtraInfoDto> getAll(Pageable pageable);

  List<ExtraInfoDto> getByType(String type);

  ExtraInfoDto getOneById(long id);
}
