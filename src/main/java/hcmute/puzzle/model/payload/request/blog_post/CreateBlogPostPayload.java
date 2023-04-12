package hcmute.puzzle.model.payload.request.blog_post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CreateBlogPostPayload {
    private String title;
    private String body;
    private String categoryBlog;
}
