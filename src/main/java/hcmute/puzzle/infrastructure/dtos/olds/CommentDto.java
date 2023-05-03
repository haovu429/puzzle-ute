package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    private long id;
    private String nickname;
    private String email;
    private String content;
    private int likeNum = 0;
    private int disLikeNum = 0;
    private long blogPostId;
}
