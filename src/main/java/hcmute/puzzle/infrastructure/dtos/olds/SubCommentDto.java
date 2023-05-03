package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubCommentDto {
    private long id;
    private String nickname;
    private String email;
    private String content;
    private String blogCategory;
    private String interact;
    private long commentId;
}
