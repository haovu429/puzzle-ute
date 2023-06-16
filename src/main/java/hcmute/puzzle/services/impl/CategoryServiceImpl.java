package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.FileStorageException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.dtos.olds.CategoryDto;
import hcmute.puzzle.infrastructure.dtos.request.CreateCategoryRequest;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.Category;
import hcmute.puzzle.infrastructure.entities.File;
import hcmute.puzzle.infrastructure.mappers.CategoryMapper;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.repository.CategoryRepository;
import hcmute.puzzle.infrastructure.repository.FileRepository;
import hcmute.puzzle.services.CategoryService;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CategoryMapper categoryMapper;

  @Autowired
  FilesStorageService filesStorageService;

  @Autowired
  FileRepository fileRepository;

  @Override
  @Transactional
  public CategoryDto save(CreateCategoryRequest createCategoryRequest) {
    Category category = Category.builder().name(createCategoryRequest.getName()).isActive(true).build();
    category = categoryRepository.save(category);
    MultipartFile multipartFile = createCategoryRequest.getImageFile();
    String imageFileName = String.format("%s_%s", category.getId(), Constant.SUFFIX_CATEGORY_IMAGE_FILE_NAME);
    String uploadedFile = filesStorageService.uploadFileWithFileTypeReturnUrl(imageFileName, multipartFile,
                                                                              FileCategory.IMAGE_CATEGORY, true)
                                             .orElseThrow(() -> new FileStorageException("Upload file fail"));
    category.setImage(uploadedFile);
    categoryRepository.save(category);
    return categoryMapper.categoryToCategoryDto(category);
  }

  @Override
  @Transactional
  public CategoryDto update(CreateCategoryRequest createCategoryRequest, long id) {
    Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException("Category not found!"));
    categoryMapper.updateCategoryFromCreateCategoryRequest(createCategoryRequest, category);
    MultipartFile multipartFile = createCategoryRequest.getImageFile();
    String imageFileName = String.format("%s_%s", category.getId(), Constant.SUFFIX_CATEGORY_IMAGE_FILE_NAME);
    String uploadedFile = filesStorageService.uploadFileWithFileTypeReturnUrl(imageFileName, multipartFile,
                                                                              FileCategory.IMAGE_CATEGORY, true)
                                             .orElseThrow(() -> new FileStorageException("Upload file fail"));
    // Delete image old
    if (Objects.nonNull(category.getImage())) {
      filesStorageService.deleteFile(category.getImage(), FileCategory.IMAGE_CATEGORY, true);
    }
    category.setImage(uploadedFile);
    categoryRepository.save(category);
    return categoryMapper.categoryToCategoryDto(category);
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
