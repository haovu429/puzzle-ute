package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.CategoryDto;
import hcmute.puzzle.infrastructure.dtos.request.CreateCategoryRequest;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;


public interface CategoryService {
    CategoryDto save(CreateCategoryRequest createCategoryRequest);

    CategoryDto update(CreateCategoryRequest createCategoryRequest, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);
}
