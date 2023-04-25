package hcmute.puzzle.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sub_comment")
public class SubCommentEntity extends Auditable {

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

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDelete = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentEntity;
}
