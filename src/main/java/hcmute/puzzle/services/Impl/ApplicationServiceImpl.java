package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.ApplicationDTO;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.ApplicationEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.ApplicationRepository;
import hcmute.puzzle.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class ApplicationServiceImpl implements ApplicationService {
  @Autowired ApplicationRepository applicationRepository;

  @Autowired Converter converter;

  @Override
  public ResponseObject findById(Long id) {
    Optional<ApplicationEntity> dto = applicationRepository.findById(id);
    if (!dto.isPresent()) {
      throw new CustomException("Can not found Address with id " + id);
    }
    return new ResponseObject(converter.toDTO(dto.get()));
  }

  @Override
  public ResponseObject deleteById(Long id) {
    Optional<ApplicationEntity> foundEntity = applicationRepository.findById(id);
    if (!foundEntity.isPresent()) {
      throw new RuntimeException("Can not found Application with id " + id);
    }
    applicationRepository.delete(foundEntity.get());
    return new ResponseObject(200, " successful delete application with id " + id, null);
  }

  @Override
  public ResponseObject findAll(Pageable pageable) {

    Page<ApplicationEntity> entities = applicationRepository.findAll(pageable);
    Page<ApplicationDTO> dtos =
        entities.map(
            new Function<ApplicationEntity, ApplicationDTO>() {
              @Override
              public ApplicationDTO apply(ApplicationEntity entity) {
                return converter.toDTO(entity);
              }
            });
    return new ResponseObject(dtos);
  }
}
