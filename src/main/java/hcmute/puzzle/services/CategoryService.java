package hcmute.puzzle.services;

import hcmute.puzzle.dto.CategoryDTO;
import hcmute.puzzle.response.DataResponse;


public interface CategoryService {
    DataResponse save(CategoryDTO dto);

    DataResponse update(CategoryDTO dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);
}
