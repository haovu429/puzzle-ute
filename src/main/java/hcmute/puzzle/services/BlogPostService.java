package hcmute.puzzle.services;

import hcmute.puzzle.dto.BlogPostDTO;
import hcmute.puzzle.dto.CategoryDTO;
import hcmute.puzzle.response.DataResponse;

public interface BlogPostService {

    DataResponse save(BlogPostDTO dto);

    DataResponse update(BlogPostDTO dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);
}
