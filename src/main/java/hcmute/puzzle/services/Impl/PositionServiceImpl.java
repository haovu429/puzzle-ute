package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.PositionDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.PositionEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.PositionRepository;
import hcmute.puzzle.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceImpl implements PositionService {
  @Autowired Converter converter;

  @Autowired PositionRepository positionRepository;

  @Override
  public ResponseObject save(PositionDTO positionDTO) {
    PositionEntity positionEntity = converter.toEntity(positionDTO);
    positionEntity = positionRepository.save(positionEntity);
    return new ResponseObject(200, "create successfully", positionEntity);
  }

  @Override
  public ResponseObject update(PositionDTO positionDTO) {

    boolean exists = positionRepository.existsById(positionDTO.getId());

    if (!exists) {
      throw new CustomException("Job post is not exist");
    }

    PositionEntity positionEntity = converter.toEntity(positionDTO);
    positionEntity = positionRepository.save(positionEntity);
    return new ResponseObject(200, "create successfully", positionEntity);
  }

  @Override
  public ResponseObject delete(long id) {
    return null;
  }

  @Override
  public ResponseObject getAll() {
    return null;
  }
}
