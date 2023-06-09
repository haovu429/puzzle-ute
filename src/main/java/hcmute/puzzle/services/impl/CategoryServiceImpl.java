package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.CategoryDto;
import hcmute.puzzle.infrastructure.entities.Category;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.CategoryRepository;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
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
  public DataResponse save(CategoryDto dto) {
    dto.setId(0);

    Category category = categoryRepository.save(converter.toEntity(dto));
    return new DataResponse(converter.toDTO(category));
  }

  @Override
  public DataResponse update(CategoryDto dto, long id) {
    if (!categoryRepository.existsById(id)) {
      throw new CustomException("Category not found!");
    }
    Category newCategory = converter.toEntity(dto);
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
    List<CategoryDto> categoryEntities =
        categoryRepository.findAll().stream()
            .map(entity -> converter.toDTO(entity))
            .collect(Collectors.toList());

    return new DataResponse(categoryEntities);
  }

  @Override
  public DataResponse getOneById(long id) {
    Optional<Category> categoryEntity = categoryRepository.findById(id);
    if (categoryEntity.isEmpty()) {
      throw new CustomException("Category not found!");
    }
    return new DataResponse(converter.toDTO(categoryEntity.get()));
  }
}
