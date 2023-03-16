package hcmute.puzzle.configuration;

import hcmute.puzzle.dto.BlogPostDTO;
import hcmute.puzzle.dto.CommentDTO;
import hcmute.puzzle.dto.SubCommentDTO;
import hcmute.puzzle.entities.BlogPostEntity;
import hcmute.puzzle.entities.CommentEntity;
import hcmute.puzzle.entities.SubCommentEntity;
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
    TypeMap<BlogPostEntity, BlogPostDTO> blogEntityToDTO =
        modelMapper.createTypeMap(BlogPostEntity.class, BlogPostDTO.class);
    // add deep mapping to flatten source's Player object into a single field in destination
    blogEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getUserEntity().getId(), BlogPostDTO::setUserId));

    //    TypeMap<BlogPostDTO, BlogPostEntity> blogDTOtoEntity =
    //        modelMapper.createTypeMap(BlogPostDTO.class, BlogPostEntity.class);
    //    // add deep mapping to flatten source's Player object into a single field in destination
    //    blogDTOtoEntity.addMappings(
    //        mapper -> mapper.map(src -> src.getUserId(), BlogPostDTO::setUserId));

    // Comment
    TypeMap<CommentEntity, CommentDTO> commentEntityToDTO =
        modelMapper.createTypeMap(CommentEntity.class, CommentDTO.class);
    commentEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getBlogPostEntity().getId(), CommentDTO::setBlogPostId));

    TypeMap<CommentDTO, CommentEntity> commentDTOtoEntity =
        modelMapper.createTypeMap(CommentDTO.class, CommentEntity.class);
    commentDTOtoEntity.addMappings(mapper -> mapper.skip(CommentEntity::setLikeNum));

    // SubComment
    TypeMap<SubCommentEntity, SubCommentDTO> subCommentEntityToDTO =
        modelMapper.createTypeMap(SubCommentEntity.class, SubCommentDTO.class);
    subCommentEntityToDTO.addMappings(
        mapper -> mapper.map(src -> src.getCommentEntity().getId(), SubCommentDTO::setCommentId));

    return modelMapper;
  }
}
