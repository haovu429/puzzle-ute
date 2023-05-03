package hcmute.puzzle.infrastructure.models.payload.request.blog_post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateBlogPostPayload {
    private String title;
    private String body;
    private String categoryBlog;
}
