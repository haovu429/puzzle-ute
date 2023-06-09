package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.news.CreateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import hcmute.puzzle.infrastructure.entities.Comment;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.CommentMapper;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.CommentRepository;
import hcmute.puzzle.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired
  Converter converter;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  BlogPostRepository blogPostRepository;

  @Autowired
  CommentMapper commentMapper;

  @Override
  public DataResponse save(CommentDto dto) {
    Comment entity = converter.toEntity(dto);
    try {
      Optional<BlogPost> blogPost = blogPostRepository.findById(dto.getBlogPostId());
      if (blogPost.isPresent()) {
        entity.setBlogPost(blogPost.get());
      } else {
        throw new CustomException("Blog post invalid");
      }
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }

    return new DataResponse(converter.toDTO(commentRepository.save(entity)));
  }

  @Override
  public DataResponse update(CommentDto dto, long id) {
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
    List<CommentDto> dtos =
        commentRepository.findAll().stream()
            .map(entity -> converter.toDTO(entity))
            .collect(Collectors.toList());
    return new DataResponse(dtos);
  }

  @Override
  public DataResponse getOneById(long id) {
      CommentDto dto = converter.toDTO(commentRepository.findById(id).get());
      return new DataResponse(dto);
  }

  @Override
  public DataResponse likeComment(long id) {
    Optional<Comment> comment = commentRepository.findById(id);
    if (comment.isEmpty()) {
      throw new CustomException("Comment invalid");
    }

    // init likeNum
    if (Objects.isNull(comment.get().getLikeNum())) {
      comment.get().setLikeNum(0);
    }

    comment.get().setLikeNum(comment.get().getLikeNum() + 1);
    commentRepository.save(comment.get());
    return new DataResponse("Success");
  }

  @Override
  public DataResponse disLikeComment(long id) {
    Optional<Comment> comment = commentRepository.findById(id);
    if (comment.isEmpty()) {
      throw new CustomException("Comment invalid");
    }

    // init likeNum
    if (Objects.isNull(comment.get().getDisLikeNum())) {
      comment.get().setDisLikeNum(0);
    }

    comment.get().setDisLikeNum(comment.get().getDisLikeNum() + 1);
    commentRepository.save(comment.get());
    return new DataResponse("Success");
  }

  @Override
  public CommentDto addComment(CreateCommentRequest createCommentRequest, long blogPostId) {

    BlogPost blogPost = blogPostRepository.findById(blogPostId)
										  .orElseThrow(() -> new NotFoundDataException("NOT FOUND BLOG POST"));

    Comment comment = commentMapper.createCommentRequestToComment(createCommentRequest);
    CustomUserDetails customUserDetail = (CustomUserDetails) SecurityContextHolder.getContext()
                                                                                  .getAuthentication()
                                                                                  .getPrincipal();
    User user = customUserDetail.getUser();
    comment.setAuthor(user);
    comment.setBlogPost(blogPost);

    commentRepository.save(comment);
    CommentDto commentDto = commentMapper.commentToCommentDto(comment);
    return commentDto;
  }
}
