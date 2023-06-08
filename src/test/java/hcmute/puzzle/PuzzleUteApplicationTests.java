package hcmute.puzzle;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.news.UserPostDto;
import hcmute.puzzle.infrastructure.entities.BlogPost;
import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.mappers.UserMapper;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import hcmute.puzzle.utils.Provider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@SpringBootTest
class PuzzleUteApplicationTests {



  @Autowired private ModelMapper modelMapper;

  @Autowired private Converter converter;

  @Autowired private RoleRepository roleRepository;

  @Test
  void contextLoads() {}

  //@Test
  void testConvertByModelMapper(){
    BlogPost blogPost = new BlogPost();

    User user = new User();
    user.setId(78L);
    user.setFullName("Hao dep try");

    blogPost.setAuthor(user);
    blogPost.setTitle("Thanh nien dep try nhat xom");

    BlogPostDto blogPostDTO = converter.toDTO(blogPost);

    assertEquals(78L, blogPostDTO.getUserId());
    assertEquals("Thanh nien dep try nhat xom", blogPostDTO.getTitle());
  }

  @Test
  void testConvertByMapStruct() {

    Set<Role> roleEntities = new HashSet<>();
    roleEntities.add(roleRepository.findByCode("user").orElse(null));

    User user = User.builder()
                    .username("haovu429")
                    .fullName("Le Vu Hao")
                    .provider(Provider.LOCAL)
                    .avatar("https://www.google.com/search?q=anne+hathaway&tbm=isch&ved=2ahUKEwjR6byc8NX-AhV1slYBHfgEBTsQ2-cCegQIABAA&oq=anne+hathaway&gs_lcp=CgNpbWcQAzIKCAAQigUQsQMQQzIECAAQAzIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQyBQgAEIAEOgcIABCKBRBDOggIABCABBCxA1CuCljuSWDvS2gCcAB4AIABnAGIAZYOkgEEMC4xNJgBAKABAaoBC2d3cy13aXotaW1nwAEB&sclient=img&ei=NZxQZJH7HfXk2roP-ImU2AM&bih=739&biw=1536&client=firefox-b-d#imgrc=l_S_oKhY86lypM")
                    .email("haovu429@gmail.com")
                    //            .roles(roleEntities)
                    .locale("VN")
                    .build();

    UserPostDto userPostDTO = UserMapper.INSTANCE.userToUserPostDto(user);
    log.info(userPostDTO.toString());
  }
}
