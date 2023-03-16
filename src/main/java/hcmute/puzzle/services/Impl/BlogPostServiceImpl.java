package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.BlogPostDTO;
import hcmute.puzzle.entities.BlogPostEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.BlogPostRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogPostServiceImpl implements BlogPostService {

  @Autowired private Converter converter;

  @Autowired private BlogPostRepository blogPostRepository;

  @Override
  public DataResponse save(BlogPostDTO dto) {
    BlogPostEntity entity = converter.toEntity(dto);
    entity = blogPostRepository.save(entity);
    return new DataResponse(converter.toDTO(entity));
  }

  @Override
  public DataResponse update(BlogPostDTO dto, long id) {
    BlogPostEntity entity = blogPostRepository.getById(id);
    if (entity != null) {
      if (dto.getUserId() != entity.getUserEntity().getId()) {
        throw new CustomException("You don't have rights for this blog post");
      }
      entity = converter.toEntity(dto);
      entity.setId(id);
      blogPostRepository.save(entity);
      return new DataResponse(converter.toDTO(entity));
    }
    throw new CustomException("Not found blog post");
  }

  @Override
  public DataResponse delete(long id) {
    blogPostRepository.deleteById(id);
    return new DataResponse("Success");
  }

  @Override
  public DataResponse getAll() {
    List<BlogPostDTO> dtos =
        blogPostRepository.findAll().stream()
            .map(
                entity -> {
                  BlogPostDTO blogPostDTO = converter.toDTO(entity);
                  blogPostDTO.setBody(null);
                  return blogPostDTO;
                })
            .collect(Collectors.toList());
    return new DataResponse(dtos);
  }

  @Override
  public DataResponse getOneById(long id) {
    BlogPostDTO dto = converter.toDTO(blogPostRepository.findById(id).get());
    return new DataResponse(dto);
  }
}
