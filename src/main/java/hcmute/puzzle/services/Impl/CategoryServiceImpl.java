package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.CategoryDTO;
import hcmute.puzzle.entities.CategoryEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.CategoryRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired Converter converter;

  @Autowired CategoryRepository categoryRepository;

  @Override
  public DataResponse save(CategoryDTO dto) {
    dto.setId(0);

    CategoryEntity categoryEntity = categoryRepository.save(converter.toEntity(dto));
    return new DataResponse(converter.toDTO(categoryEntity));
  }

  @Override
  public DataResponse update(CategoryDTO dto, long id) {
    if (!categoryRepository.existsById(id)) {
      throw new CustomException("Category not found!");
    }
    CategoryEntity newCategory = converter.toEntity(dto);
    newCategory.setId(id);
    categoryRepository.save(newCategory);

    return new DataResponse(converter.toDTO(newCategory));
  }

  @Override
  public DataResponse delete(long id) {
    if (!categoryRepository.existsById(id)) {
      throw new CustomException("Category not found!");
    }
    categoryRepository.deleteById(id);
    return new DataResponse("Delete successfully");
  }

  @Override
  public DataResponse getAll() {
    List<CategoryDTO> categoryEntities =
        categoryRepository.findAll().stream()
            .map(entity -> converter.toDTO(entity))
            .collect(Collectors.toList());

    return new DataResponse(categoryEntities);
  }

  @Override
  public DataResponse getOneById(long id) {
    Optional<CategoryEntity> categoryEntity = categoryRepository.findById(id);
    if (categoryEntity.isEmpty()) {
      throw new CustomException("Category not found!");
    }
    return new DataResponse(converter.toDTO(categoryEntity.get()));
  }
}
