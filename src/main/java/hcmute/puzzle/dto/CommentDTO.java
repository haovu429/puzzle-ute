package hcmute.puzzle.dto;

import hcmute.puzzle.entities.BlogPostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
public class CommentDTO {

    private long id;
    private String nickname;
    private String email;
    private String content;
    private int likeNum = 0;
    private int disLikeNum = 0;
    private long blogPostId;
}
