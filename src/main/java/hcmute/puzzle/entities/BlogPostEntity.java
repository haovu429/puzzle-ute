package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "blog_post")
public class BlogPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", columnDefinition = "VARCHAR(200)")
    private String title;

    @Column(name = "body", columnDefinition = "Text")
    private String body;

    @Column(name = "create_time")
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "last_update")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Column(name = "category_blog", columnDefinition = "VARCHAR(200)")
    private String categoryBlog;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "created_user")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "blogPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CommentEntity> commentEntities = new HashSet<>();

}
