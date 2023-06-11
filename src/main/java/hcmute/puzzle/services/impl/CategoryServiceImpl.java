package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.CategoryDto;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.Category;
import hcmute.puzzle.infrastructure.mappers.CategoryMapper;
import hcmute.puzzle.infrastructure.repository.CategoryRepository;
import hcmute.puzzle.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CategoryMapper categoryMapper;

  @Override
  public CategoryDto save(CategoryDto dto) {
    dto.setId(0);

    Category category = categoryRepository.save(categoryMapper.categoryDtoToCategory(dto));
    return categoryMapper.categoryToCategoryDto(category);
  }

  @Override
  public void update(CategoryDto categoryDto, long id) {
    Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException("Category not found!"));
    categoryMapper.updateCategoryFromDto(categoryDto, category);
    categoryRepository.save(category);
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
    List<CategoryDto> categoryEntities = categoryRepository.findAll()
                                                           .stream()
                                                           .map(entity -> categoryMapper.categoryToCategoryDto(entity))
                                                           .collect(Collectors.toList());

    return new DataResponse(categoryEntities);
  }

  @Override
  public DataResponse getOneById(long id) {
    Category categoryEntity = categoryRepository.findById(id)
                                                .orElseThrow(() -> new NotFoundDataException("Category not found!"));
    return new DataResponse(categoryMapper.categoryToCategoryDto(categoryEntity));
  }
}
