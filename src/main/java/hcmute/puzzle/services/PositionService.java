package hcmute.puzzle.services;

import hcmute.puzzle.dto.PositionDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.dto.ServiceDTO;

public interface PositionService {
    ResponseObject save(PositionDTO positionDTO);

    ResponseObject update(PositionDTO positionDTO);

    ResponseObject delete(long id);

    ResponseObject getAll();
}
