package hcmute.puzzle.configuration;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.olds.SubCommentDto;
import hcmute.puzzle.infrastructure.entities.BlogPostEntity;
import hcmute.puzzle.infrastructure.entities.CommentEntity;
import hcmute.puzzle.infrastructure.entities.SubCommentEntity;
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
    TypeMap<BlogPostEntity, BlogPostDto> blogEntityToDTO =
        modelMapper.createTypeMap(BlogPostEntity.class, BlogPostDto.class);
    // add deep mapping to flatten source's Player object into a single field in destination
    blogEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getAuthor().getId(), BlogPostDto::setUserId));

    //    TypeMap<BlogPostDto, BlogPostEntity> blogDTOtoEntity =
    //        modelMapper.createTypeMap(BlogPostDto.class, BlogPostEntity.class);
    //    // add deep mapping to flatten source's Player object into a single field in destination
    //    blogDTOtoEntity.addMappings(
    //        mapper -> mapper.map(src -> src.getUserId(), BlogPostDto::setUserId));

    // Comment
    TypeMap<CommentEntity, CommentDto> commentEntityToDTO =
        modelMapper.createTypeMap(CommentEntity.class, CommentDto.class);
    commentEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getBlogPostEntity().getId(), CommentDto::setBlogPostId));

    TypeMap<CommentDto, CommentEntity> commentDTOtoEntity =
        modelMapper.createTypeMap(CommentDto.class, CommentEntity.class);
    commentDTOtoEntity.addMappings(mapper -> mapper.skip(CommentEntity::setLikeNum));

    // SubComment
    TypeMap<SubCommentEntity, SubCommentDto> subCommentEntityToDTO =
        modelMapper.createTypeMap(SubCommentEntity.class, SubCommentDto.class);
    subCommentEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getComment().getId(), SubCommentDto::setCommentId));

    return modelMapper;
  }
}
