package hcmute.puzzle.services;

import hcmute.puzzle.dto.PositionDTO;
import hcmute.puzzle.dto.ResponseObject;

public interface PositionService {
  ResponseObject save(PositionDTO positionDTO);

  ResponseObject update(PositionDTO positionDTO);

  ResponseObject delete(long id);

  ResponseObject getAll();
}
