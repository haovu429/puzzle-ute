package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sub_comment")
public class SubCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nickname", columnDefinition = "VARCHAR(50)")
    private String nickname;

    @Column(name = "email", columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(name = "content", columnDefinition = "VARCHAR(200)")
    private String content;

    @Column(name = "blog_category", columnDefinition = "VARCHAR(100)")
    private String blogCategory;

    @Column(name = "interact", columnDefinition = "VARCHAR(30)")
    private String interact;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentEntity;
}
