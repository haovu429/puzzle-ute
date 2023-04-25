package hcmute.puzzle.services.impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ExtraInfoDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.ExtraInfoEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.ExtraInfoRepository;
import hcmute.puzzle.services.ExtraInfoService;
import hcmute.puzzle.utils.Constant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtraInfoServiceImpl implements ExtraInfoService {

  @Autowired ExtraInfoRepository extraInfoRepository;

  @Autowired Converter converter;

  @Override
  public ResponseObject save(ExtraInfoDTO extraInfoDTO) {
    extraInfoDTO.setType(Constant.validateTypeExtraInfo(extraInfoDTO.getType().toUpperCase()));

    ExtraInfoEntity extraInfoEntity = converter.toEntity(extraInfoDTO);
    extraInfoEntity = extraInfoRepository.save(extraInfoEntity);

    return new ResponseObject(200, "Save successfully", converter.toDTO(extraInfoEntity));
  }

  @Override
  public ResponseObject update(ExtraInfoDTO extraInfoDTO, long extraInfoId) {
    boolean exists = extraInfoRepository.existsById(extraInfoId);
    extraInfoDTO.setType(Constant.validateTypeExtraInfo(extraInfoDTO.getType().toUpperCase()));

    if (!exists) {
      throw new CustomException("ExtraInfo isn't exists");
    }

    ExtraInfoEntity extraInfoEntity = converter.toEntity(extraInfoDTO);
    extraInfoEntity = extraInfoRepository.save(extraInfoEntity);
    return new ResponseObject(200, "Update successfully", extraInfoEntity);
  }

  @Override
  public ResponseObject delete(long id) {
    boolean exists = extraInfoRepository.existsById(id);

    if (!exists) {
      throw new CustomException("ExtraInfo isn't exists");
    }

    extraInfoRepository.deleteById(id);

    return new ResponseObject(200, "Delete successfully", null);
  }

  @Override
  public ResponseObject getAll() {
    Set<ExtraInfoEntity> extraInfoEntities = new HashSet<>();
    extraInfoEntities.addAll(extraInfoRepository.findAll());

    return new ResponseObject(200, "ExtraInfo", extraInfoEntities);
  }

  @Override
  public ResponseObject getByType(String type) {
    Set<ExtraInfoEntity> extraInfoEntities = new HashSet<>();
    extraInfoEntities.addAll(extraInfoRepository.getExtraInfoEntitiesByType(type.toUpperCase()));

    return new ResponseObject(200, "ExtraInfo by type", extraInfoEntities);
  }

  @Override
  public ResponseObject getOneById(long id) {
    Optional<ExtraInfoEntity> extraInfo = extraInfoRepository.findById(id);
    if (extraInfo.isEmpty()) {
      throw new CustomException("Extra info isn't exists");
    }
    return new ResponseObject(200, "ExtraInfo", extraInfo.get());
  }
}
