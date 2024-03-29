package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SubCommentDto {
    private long id;
    private String nickname;
    private String email;
    private String content;
    private String avatar;
    private String interact;
    //custom while use, resolve to info front end know current user has right to edit comment.
    private boolean canEdit;
    private Long commentId;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;
}
