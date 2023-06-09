package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.CategoryDto;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;


public interface CategoryService {
    DataResponse save(CategoryDto dto);

    DataResponse update(CategoryDto dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);
}
