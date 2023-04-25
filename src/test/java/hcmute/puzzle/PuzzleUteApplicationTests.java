package hcmute.puzzle;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.BlogPostDTO;
import hcmute.puzzle.entities.BlogPostEntity;
import hcmute.puzzle.entities.UserEntity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PuzzleUteApplicationTests {

  @Autowired private ModelMapper modelMapper;

  @Autowired private Converter converter;

  @Test
  void contextLoads() {}

  @Test
  void testConvertByModelMapper(){
    BlogPostEntity blogPostEntity = new BlogPostEntity();

    UserEntity userEntity = new UserEntity();
    userEntity.setId(78L);
    userEntity.setFullName("Hao dep try");

    blogPostEntity.setAuthor(userEntity);
    blogPostEntity.setTitle("Thanh nien dep try nhat xom");

    BlogPostDTO blogPostDTO = converter.toDTO(blogPostEntity);

    assertEquals(78L, blogPostDTO.getUserId());
    assertEquals("Thanh nien dep try nhat xom", blogPostDTO.getTitle());
  }
}
