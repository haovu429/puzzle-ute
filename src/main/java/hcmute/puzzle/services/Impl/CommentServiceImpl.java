package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.BlogPostDTO;
import hcmute.puzzle.dto.CommentDTO;
import hcmute.puzzle.entities.BlogPostEntity;
import hcmute.puzzle.entities.CommentEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.repository.BlogPostRepository;
import hcmute.puzzle.repository.CommentRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired Converter converter;

  @Autowired CommentRepository commentRepository;

  @Autowired
  BlogPostRepository blogPostRepository;

  @Override
  public DataResponse save(CommentDTO dto) {
    CommentEntity entity = converter.toEntity(dto);
    try {
      Optional<BlogPostEntity> blogPost = blogPostRepository.findById(dto.getBlogPostId());
      if (blogPost.isPresent()) {
        entity.setBlogPostEntity(blogPost.get());
      } else {
        throw new CustomException("Blog post invalid");
      }
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }

    return new DataResponse(converter.toDTO(commentRepository.save(entity)));
  }

  @Override
  public DataResponse update(CommentDTO dto, long id) {
//    Optional<CommentEntity> entity = commentRepository.findById(id);
//    if (entity.isPresent()) {
//      CommentEntity comment = entity.get();
//      comment = converter.toEntity(dto);
//      comment.setId(id);
//      commentRepository.save(comment);
//      return new DataResponse(converter.toDTO(comment));
//    }
//    throw new CustomException("Not found comment");
    return null;
  }

  @Override
  public DataResponse delete(long id) {
    commentRepository.deleteById(id);
    return new DataResponse("Success");
  }

  @Override
  public DataResponse getAll() {
    List<CommentDTO> dtos =
        commentRepository.findAll().stream()
            .map(entity -> converter.toDTO(entity))
            .collect(Collectors.toList());
    return new DataResponse(dtos);
  }

  @Override
  public DataResponse getOneById(long id) {
      CommentDTO dto = converter.toDTO(commentRepository.findById(id).get());
      return new DataResponse(dto);
  }

  @Override
  public DataResponse likeComment(long id) {
    Optional<CommentEntity> comment = commentRepository.findById(id);
    if (comment.isEmpty()) {
      throw new CustomException("Comment invalid");
    }
    comment.get().setLikeNum(comment.get().getLikeNum()+1);
    commentRepository.save(comment.get());
    return new DataResponse("Success");
  }

  @Override
  public DataResponse disLikeComment(long id) {
    Optional<CommentEntity> comment = commentRepository.findById(id);
    if (comment.isEmpty()) {
      throw new CustomException("Comment invalid");
    }
    comment.get().setDisLikeNum(comment.get().getDisLikeNum()+1);
    commentRepository.save(comment.get());
    return new DataResponse("Success");
  }
}
