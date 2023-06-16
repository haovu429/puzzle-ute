package hcmute.puzzle.configuration;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.olds.SubCommentDto;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import hcmute.puzzle.infrastructure.entities.Comment;
import hcmute.puzzle.infrastructure.entities.SubComment;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
  @Bean
  public ModelMapper modelMapper() {
    // Tạo object và cấu hình
    ModelMapper modelMapper = new ModelMapper();
    modelMapper
        .getConfiguration()
        .setMatchingStrategy(MatchingStrategies.STRICT)
        .setSkipNullEnabled(true);

    // setup
    // BlogPost
    TypeMap<BlogPost, BlogPostDto> blogEntityToDTO =
        modelMapper.createTypeMap(BlogPost.class, BlogPostDto.class);
    // add deep mapping to flatten source's Player object into a single field in destination
    blogEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getAuthor().getId(), BlogPostDto::setUserId));

    //    TypeMap<BlogPostDto, BlogPostEntity> blogDTOtoEntity =
    //        modelMapper.createTypeMap(BlogPostDto.class, BlogPostEntity.class);
    //    // add deep mapping to flatten source's Player object into a single field in destination
    //    blogDTOtoEntity.addMappings(
    //        mapper -> mapper.map(src -> src.getUserId(), BlogPostDto::setUserId));

    // Comment
    TypeMap<Comment, CommentDto> commentEntityToDTO =
        modelMapper.createTypeMap(Comment.class, CommentDto.class);
    commentEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getBlogPost().getId(), CommentDto::setBlogPostId));

    TypeMap<CommentDto, Comment> commentDTOtoEntity =
        modelMapper.createTypeMap(CommentDto.class, Comment.class);
    commentDTOtoEntity.addMappings(mapper -> mapper.skip(Comment::setLikeNum));

    // SubComment
    TypeMap<SubComment, SubCommentDto> subCommentEntityToDTO =
        modelMapper.createTypeMap(SubComment.class, SubCommentDto.class);
    subCommentEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getComment().getId(), SubCommentDto::setCommentId));

    return modelMapper;
  }
}
