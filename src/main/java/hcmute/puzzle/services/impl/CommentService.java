package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.exception.UnauthorizedException;
import hcmute.puzzle.infrastructure.dtos.news.CreateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.news.CreateSubCommentRequest;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.olds.SubCommentDto;
import hcmute.puzzle.infrastructure.dtos.request.UpdateCommentRequest;
import hcmute.puzzle.infrastructure.dtos.request.UpdateSubCommentRequest;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import hcmute.puzzle.infrastructure.entities.Comment;
import hcmute.puzzle.infrastructure.entities.SubComment;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.CommentMapper;
import hcmute.puzzle.infrastructure.mappers.SubCommentMapper;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.CommentRepository;
import hcmute.puzzle.infrastructure.repository.SubCommentRepository;
import hcmute.puzzle.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommentService {
  //  @Autowired
  //  Converter converter;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  BlogPostRepository blogPostRepository;

  @Autowired
  CommentMapper commentMapper;

  @Autowired
  SubCommentRepository subCommentRepository;

  @Autowired
  SubCommentMapper subCommentMapper;

  @Autowired
  CurrentUserService currentUserService;

  //
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


  public void delete(long id) {
    commentRepository.deleteById(id);
  }


  public Page<CommentDto> getAll(Pageable pageable) {
    Page<CommentDto> commentDtos = commentRepository.findAll(pageable).map(commentMapper::commentToCommentDto);
    return commentDtos;
  }


  public CommentDto getOneById(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found comment"));
    CommentDto commentDto = commentMapper.commentToCommentDto(comment);
    return commentDto;
  }

  public List<CommentDto> getAllByBlogPostId(long blogPostId) {
    List<Comment> comments = commentRepository.findAllByBlogPostIdOrderByCreatedAtDesc(blogPostId);
    Optional<User> optionalUser = currentUserService.getCurrentUserOptional();
    User currentUser;
    if (optionalUser.isPresent()) {
      currentUser = optionalUser.get();
    } else {
      currentUser = null;
    }
    comments.forEach(comment -> {
      List<SubComment> subComments = this.findSubCommentByCommentId(comment.getId());
      comment.setSubComments(subComments);
    });
    List<CommentDto> commentDtos = commentMapper.commentListToCommentDtoList(comments);
    commentDtos.forEach(commentDto -> {
      this.resolveBlogPostDtoWithRightEditOfComment(commentDto, currentUser);
    });
    return commentDtos;
  }

  public CommentDto findById(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found comment"));
    List<SubComment> subComments = this.findSubCommentByCommentId(comment.getId());
    comment.setSubComments(subComments);
    CommentDto commentDto = commentMapper.commentToCommentDto(comment);
    return commentDto;
  }

  public List<SubComment> findSubCommentByCommentId(long commentId) {
    return subCommentRepository.findAllByCommentId(commentId, Sort.by(Sort.Direction.DESC, Constant.SortBy.CREATED_AT));
  }


  public void likeComment(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found commnet"));

    // init likeNum
    if (Objects.isNull(comment.getLikeNum())) {
      comment.setLikeNum(0);
    }
    comment.setLikeNum(comment.getLikeNum() + 1);
    commentRepository.save(comment);
  }


  public void disLikeComment(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundDataException("Not found commnet"));
    // init likeNum
    if (Objects.isNull(comment.getDisLikeNum())) {
      comment.setDisLikeNum(0);
    }
    comment.setDisLikeNum(comment.getDisLikeNum() + 1);
    commentRepository.save(comment);
  }

  public void resolveBlogPostDtoWithRightEditOfComment(CommentDto commentDto, User user) {
    if (user != null && Objects.equals(commentDto.getUserId(), user.getId())) {
      commentDto.setCanEdit(true);
    } else {
      commentDto.setCanEdit(false);
    }
    commentDto.getSubComments().forEach(subCommentDto -> {
      resolveBlogPostDtoWithRightEditOfSubComment(subCommentDto, user);
    });

  }


  public void resolveBlogPostDtoWithRightEditOfSubComment(SubCommentDto subCommentDto, User user) {
    if (user != null && Objects.equals(subCommentDto.getUserId(), user.getId())) {
      subCommentDto.setCanEdit(true);
    } else {
      subCommentDto.setCanEdit(false);
    }
  }

  public List<SubComment> sortSubCommentByCreatedAt(List<SubComment> subComments) {
    List<SubComment> sortedList = subComments.stream()
                                             .sorted(Comparator.comparing(SubComment::getCreatedAt).reversed())
                                             .toList();
    return sortedList;
  }


  public CommentDto addComment(CreateCommentRequest createCommentRequest, long blogPostId) {

    BlogPost blogPost = blogPostRepository.findById(blogPostId)
                                          .orElseThrow(() -> new NotFoundDataException("NOT FOUND BLOG POST"));

    Comment comment = commentMapper.createCommentRequestToComment(createCommentRequest);
    User currentUser = currentUserService.getCurrentUserOptional()
                                         .orElseThrow(() -> new UnauthorizedException("No current user detected"));
    if (currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()) {
      comment.setNickname(currentUser.getFullName());
    } else if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
      comment.setNickname(currentUser.getEmail().split("@")[0]);
    }
    comment.setEmail(currentUser.getEmail());
    comment.setAuthor(currentUser);
    comment.setBlogPost(blogPost);
    commentRepository.save(comment);
    CommentDto commentDto = commentMapper.commentToCommentDto(comment);

    List<SubCommentDto> subCommentDtos = subCommentMapper.subCommentListToSubCommentDtoList(
            this.findSubCommentByCommentId(comment.getId()));
    commentDto.setSubComments(subCommentDtos);
    // for rights
    this.resolveBlogPostDtoWithRightEditOfComment(commentDto, currentUser);
    return commentDto;
  }

  public CommentDto updateComment(long commentId, UpdateCommentRequest updateCommentRequest) {
    Comment comment = commentRepository.findById(commentId)
                                       .orElseThrow(() -> new NotFoundDataException("Not found comment"));

    User currentUser = currentUserService.getCurrentUser();
    if (currentUser.getId() == comment.getAuthor().getId()) {
      commentMapper.updateCommentFromUpdateCommentRequest(updateCommentRequest, comment);
      commentRepository.save(comment);
      CommentDto commentDto = commentMapper.commentToCommentDto(comment);

      List<SubCommentDto> subCommentDtos = subCommentMapper.subCommentListToSubCommentDtoList(
              this.findSubCommentByCommentId(commentId));
      commentDto.setSubComments(subCommentDtos);
      // for rights
      this.resolveBlogPostDtoWithRightEditOfComment(commentDto, currentUser);
      return commentDto;
    } else {
      throw new UnauthorizedException("You don't have rights for comment");
    }
  }

  public List<SubCommentDto> sortSubCommentDtoByCreatedAt(List<SubCommentDto> subComments) {
    List<SubCommentDto> sortedList = subComments.stream()
                                                .sorted(Comparator.comparing(SubCommentDto::getCreatedAt).reversed())
                                                .toList();
    return sortedList;
  }


  @Transactional
  public void deleteComment(long commentId) {
    User currentUser = currentUserService.getCurrentUser();
    Comment comment = commentRepository.findById(commentId)
                                       .orElseThrow(() -> new NotFoundDataException("Not found comment"));
    if (comment.getAuthor().getId() == currentUser.getId()) {
      // remove sub comment previous
      subCommentRepository.deleteByCommentId(comment.getId());
      commentRepository.delete(comment);
    } else {
      throw new UnauthorizedException("You don't have rights for this comment");
    }
  }

  @Transactional
  public void deleteSubComment(Long subCommentId) {
    User currentUser = currentUserService.getCurrentUser();
    SubComment subComment = subCommentRepository.findById(subCommentId)
                                                .orElseThrow(() -> new NotFoundDataException("Not found sub comment"));
    if (subComment.getAuthor().getId() == currentUser.getId()) {
      // remove sub comment previous
      subCommentRepository.delete(subComment);
    } else {
      throw new UnauthorizedException("You don't have rights for this sub comment");
    }
  }

  public SubCommentDto addSubComment(CreateSubCommentRequest createCommentRequest, long commentId) {

    Comment comment = commentRepository.findById(commentId)
                                       .orElseThrow(() -> new NotFoundDataException("Not found comment"));

    SubComment subComment = subCommentMapper.createCommentRequestToComment(createCommentRequest);
    User user = currentUserService.getCurrentUser();
    if (user.getFullName() != null && !user.getFullName().isEmpty()) {
      subComment.setNickname(user.getFullName());
    } else if (user.getEmail() != null && !user.getEmail().isEmpty()) {
      subComment.setNickname(user.getEmail().split("@")[0]);
    }
    subComment.setEmail(user.getEmail());
    subComment.setAuthor(user);
    subComment.setComment(comment);
    comment.getSubComments().add(subComment);
    //commentRepository.save(comment);
    subCommentRepository.save(subComment);
    SubCommentDto subCommentDto = subCommentMapper.subCommentToSubCommentDto(subComment);
    this.resolveBlogPostDtoWithRightEditOfSubComment(subCommentDto, user);
    return subCommentDto;
  }

  public SubCommentDto updateSubComment(long commentId, UpdateSubCommentRequest updateSubCommentRequest) {
    SubComment subComment = subCommentRepository.findById(commentId)
                                                .orElseThrow(() -> new NotFoundDataException("Not found sub comment"));

    User currentUser = currentUserService.getCurrentUser();
    if (currentUser.getId() == subComment.getAuthor().getId()) {
      subCommentMapper.updateCommentFromUpdateSubCommentRequest(updateSubCommentRequest, subComment);
      subCommentRepository.save(subComment);
      SubCommentDto subCommentDto = subCommentMapper.subCommentToSubCommentDto(subComment);
      // for check rights
      this.resolveBlogPostDtoWithRightEditOfSubComment(subCommentDto, currentUser);
      return subCommentMapper.subCommentToSubCommentDto(subComment);
    } else {
      throw new UnauthorizedException("You don't have rights for this sub comment");
    }

  }

}
