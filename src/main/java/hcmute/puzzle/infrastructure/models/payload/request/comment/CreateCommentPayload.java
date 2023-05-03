package hcmute.puzzle.infrastructure.models.payload.request.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCommentPayload {
    private String nickname;
    private String email;
    private String content;
}
