package hcmute.puzzle.infrastructure.dtos.olds;

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
    private Long blogCategoryId;
    private String tags;
    private Date createdAt;
    private Date updatedAt;
    private Long userId;
    private List<CommentDto> comments;
    private Boolean isActive;
    private Boolean isPublic;
}
