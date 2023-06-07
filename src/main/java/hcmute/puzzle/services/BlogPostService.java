package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostUpdateRequest;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

import java.util.List;

public interface BlogPostService {

    DataResponse createBlogPost(BlogPostRequest blogPostRequest);

    DataResponse update(BlogPostUpdateRequest blogPostUpdateRequest, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);

    List<BlogPostDto> getBlogPostByUser(long userId);
}
