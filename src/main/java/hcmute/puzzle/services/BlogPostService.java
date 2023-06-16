package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostFilterRequest;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostUpdateRequest;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import hcmute.puzzle.infrastructure.models.BlogCategoryResponseWithBlogPostAmount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogPostService {

    List<BlogCategoryResponseWithBlogPostAmount> getBlogCategoryWithBlogPostAmount();

    DataResponse createBlogPost(BlogPostRequest blogPostRequest);

    DataResponse update(BlogPostUpdateRequest blogPostUpdateRequest, long id);

    DataResponse delete(long id);

    DataResponse getAll();

    DataResponse getOneById(long id);

    List<BlogPostDto> getBlogPostByUser(long userId);

    Page<BlogPost> filterBlogPost(BlogPostFilterRequest blogPostFilterRequest, Pageable pageable);
}
