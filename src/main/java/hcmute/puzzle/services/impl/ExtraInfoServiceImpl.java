package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.dtos.olds.ResponseObject;
import hcmute.puzzle.infrastructure.entities.ExtraInfo;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.ExtraInfoRepository;
import hcmute.puzzle.services.ExtraInfoService;
import hcmute.puzzle.utils.Constant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtraInfoServiceImpl implements ExtraInfoService {

  @Autowired ExtraInfoRepository extraInfoRepository;

  @Autowired Converter converter;

  @Override
  public ResponseObject save(ExtraInfoDto extraInfoDTO) {
    extraInfoDTO.setType(Constant.validateTypeExtraInfo(extraInfoDTO.getType().toUpperCase()));

    ExtraInfo extraInfo = converter.toEntity(extraInfoDTO);
    extraInfo = extraInfoRepository.save(extraInfo);

    return new ResponseObject(200, "Save successfully", converter.toDTO(extraInfo));
  }

  @Override
  public ResponseObject update(ExtraInfoDto extraInfoDTO, long extraInfoId) {
    boolean exists = extraInfoRepository.existsById(extraInfoId);
    extraInfoDTO.setType(Constant.validateTypeExtraInfo(extraInfoDTO.getType().toUpperCase()));

    if (!exists) {
      throw new CustomException("ExtraInfo isn't exists");
    }

    ExtraInfo extraInfo = converter.toEntity(extraInfoDTO);
    extraInfo = extraInfoRepository.save(extraInfo);
    return new ResponseObject(200, "Update successfully", extraInfo);
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
    Set<ExtraInfo> extraInfoEntities = new HashSet<>();
    extraInfoEntities.addAll(extraInfoRepository.findAll());

    return new ResponseObject(200, "ExtraInfo", extraInfoEntities);
  }

  @Override
  public ResponseObject getByType(String type) {
    Set<ExtraInfo> extraInfoEntities = new HashSet<>();
    extraInfoEntities.addAll(extraInfoRepository.getExtraInfoEntitiesByType(type.toUpperCase()));
    Set<ExtraInfoDto> extraInfoDtos = extraInfoEntities.stream().map(ext -> converter.toDTO(ext)).collect(Collectors.toSet());
    return new ResponseObject(200, "ExtraInfo by type", extraInfoDtos);
  }

  @Override
  public ResponseObject getOneById(long id) {
    Optional<ExtraInfo> extraInfo = extraInfoRepository.findById(id);
    if (extraInfo.isEmpty()) {
      throw new CustomException("Extra info isn't exists");
    }
    return new ResponseObject(200, "ExtraInfo", converter.toDTO(extraInfo.get()));
  }
}
