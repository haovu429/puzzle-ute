package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

public interface BlogPostService {

    DataResponse createBlogPost(BlogPostDto dto);

    DataResponse update(BlogPostDto dto, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);
}
