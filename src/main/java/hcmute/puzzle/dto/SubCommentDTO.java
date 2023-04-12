package hcmute.puzzle.dto;

import hcmute.puzzle.entities.CommentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
public class SubCommentDTO {
    private long id;
    private String nickname;
    private String email;
    private String content;
    private String blogCategory;
    private String interact;
    private long commentId;
}
