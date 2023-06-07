package hcmute.puzzle.infrastructure.dtos.olds;

import com.sun.istack.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPostDto {
    private long id;
    private String title;
    private String body;
    private String thumbnail;
    private long categoryId;
    private String tags;
    private Date createdAt;
    private Date updatedAt;
    private long userId;
    private List<CommentDto> comments;
}
