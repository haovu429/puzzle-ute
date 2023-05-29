package hcmute.puzzle.infrastructure.entities;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Builder
@AllArgsConstructor
@Table(name = "comment")
public class CommentEntity extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nickname", columnDefinition = "VARCHAR(50)")
    private String nickname;

    @Column(name = "email", columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(name = "content", columnDefinition = "VARCHAR(200)")
    private String content;

    @Column(name = "like_num")
    private Integer likeNum;

    @Column(name = "dis_like_num")
    private Integer disLikeNum;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "blog_post_id")
    private BlogPostEntity blogPostEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SubCommentEntity> subComments = new HashSet<>();
}
