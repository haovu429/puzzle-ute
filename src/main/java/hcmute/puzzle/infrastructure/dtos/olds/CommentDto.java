package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentDto {

    private long id;
    private String nickname;
    private String email;
    private String avatar;
    private String content;
    private Integer likeNum;
    private Integer disLikeNum;
    private Long blogPostId;
    private Date createdAt;
    private Date updatedAt;
    private Long userId;
    //custom while use, resolve to info front end know current user has right to edit comment.
    private boolean canEdit;
    private List<SubCommentDto> subComments;
}
