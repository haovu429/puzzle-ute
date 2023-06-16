package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.dtos.news.CreateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import hcmute.puzzle.infrastructure.entities.Comment;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.CommentMapper;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.CommentRepository;
import hcmute.puzzle.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CommentServiceImpl implements CommentService {
  //  @Autowired
  //  Converter converter;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  BlogPostRepository blogPostRepository;

  @Autowired
  CommentMapper commentMapper;

  //  @Override
  //  public CommentDto save(CreateCommentRequest createCommentRequest) {
  //    Comment comment = commentMapper.createCommentRequestToComment(createCommentRequest);
  //    try {
  //      Optional<BlogPost> blogPost = blogPostRepository.findById(createCommentRequest.getBlogPostId());
  //      if (blogPost.isPresent()) {
  //        comment.setBlogPost(blogPost.get());
  //      } else {
  //        throw new CustomException("Blog post invalid");
  //      }
  //    } catch (Exception e) {
  //      throw new CustomException(e.getMessage());
  //    }
  //    commentRepository.save(comment);
  //
  //    return commentMapper.commentToCommentDto(comment);
  //  }

  @Override
  public CommentDto update(CommentDto dto, long id) {
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
  public void delete(long id) {
    commentRepository.deleteById(id);
  }

  @Override
  public Page<CommentDto> getAll(Pageable pageable) {
    Page<CommentDto> commentDtos = commentRepository.findAll(pageable).map(commentMapper::commentToCommentDto);
    return commentDtos;
  }

  @Override
  public CommentDto getOneById(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found comment"));
    CommentDto commentDto = commentMapper.commentToCommentDto(comment);
    return commentDto;
  }

  @Override
  public void likeComment(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found commnet"));

    // init likeNum
    if (Objects.isNull(comment.getLikeNum())) {
      comment.setLikeNum(0);
    }
    comment.setLikeNum(comment.getLikeNum() + 1);
    commentRepository.save(comment);
  }

  @Override
  public void disLikeComment(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found commnet"));
    // init likeNum
    if (Objects.isNull(comment.getDisLikeNum())) {
      comment.setDisLikeNum(0);
    }
    comment.setDisLikeNum(comment.getDisLikeNum() + 1);
    commentRepository.save(comment);
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
