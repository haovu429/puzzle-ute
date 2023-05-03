package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BlogPostDto {
    private long id;
    private String title;
    private String body;
    private Date createTime;
    private Date lastUpdate;
    private String categoryBlog;
    private long userId;
}
