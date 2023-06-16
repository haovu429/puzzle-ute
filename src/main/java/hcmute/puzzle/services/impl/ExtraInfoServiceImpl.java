package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.dtos.olds.ExtraInfoDto;
import hcmute.puzzle.infrastructure.entities.ExtraInfo;
import hcmute.puzzle.infrastructure.mappers.ExtraInfoMapper;
import hcmute.puzzle.infrastructure.models.enums.ExtraInfoType;
import hcmute.puzzle.infrastructure.repository.ExtraInfoRepository;
import hcmute.puzzle.services.ExtraInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExtraInfoServiceImpl implements ExtraInfoService {

  @Autowired
  ExtraInfoRepository extraInfoRepository;

  //@Autowired Converter converter;

  @Autowired
  ExtraInfoMapper extraInfoMapper;

  @Override
  public ExtraInfoDto save(ExtraInfoDto extraInfoDTO) {
    //extraInfoDTO.setType(Constant.validateTypeExtraInfo(extraInfoDTO.getType().toUpperCase()));
    ExtraInfo extraInfo = extraInfoMapper.extraInfoDtoToExtraInfo(extraInfoDTO);
    extraInfo = extraInfoRepository.save(extraInfo);
    ExtraInfoDto employerDto = extraInfoMapper.extraInfoToExtraInfoDto(extraInfo);

    return employerDto;
  }

  @Override
  public ExtraInfoDto update(ExtraInfoDto extraInfoDTO, long extraInfoId) {
    ExtraInfo extraInfo = extraInfoRepository.findById(extraInfoId)
                                             .orElseThrow(() -> new NotFoundDataException("ExtraInfo isn't exists"));
    // extraInfoDTO.setType(Constant.validateTypeExtraInfo(extraInfoDTO.getType().toUpperCase()));

    extraInfoMapper.updateExtraInfoFromExtraInfoDto(extraInfoDTO, extraInfo);
    extraInfo = extraInfoRepository.save(extraInfo);
    return extraInfoMapper.extraInfoToExtraInfoDto(extraInfo);
  }

  @Override
  public void delete(long id) {
    ExtraInfo extraInfo = extraInfoRepository.findById(id)
                                             .orElseThrow(() -> new NotFoundDataException("ExtraInfo isn't exists"));

    extraInfoRepository.delete(extraInfo);
  }

  @Override
  public Page<ExtraInfoDto> getAll(Pageable pageable) {
    Page<ExtraInfo> extraInfoEntities = extraInfoRepository.findAll(pageable);
    Page<ExtraInfoDto> extraInfoDtos = extraInfoEntities.map(extraInfoMapper::extraInfoToExtraInfoDto);
    return extraInfoDtos;
  }

  @Override
  public List<ExtraInfoDto> getByType(String type) {
    List<ExtraInfo> extraInfoEntities = new ArrayList<>();
    try {
      extraInfoEntities =
              extraInfoRepository.getExtraInfoEntitiesByType(ExtraInfoType.valueOf(type.toUpperCase()));
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage(), e);
    }

    return extraInfoEntities.stream().map(extraInfoMapper::extraInfoToExtraInfoDto).toList();
  }

  @Override
  public ExtraInfoDto getOneById(long id) {
    ExtraInfo extraInfo = extraInfoRepository.findById(id)
                                             .orElseThrow(() -> new NotFoundDataException("Extra info isn't exists"));

    return extraInfoMapper.extraInfoToExtraInfoDto(extraInfo);
  }
}
