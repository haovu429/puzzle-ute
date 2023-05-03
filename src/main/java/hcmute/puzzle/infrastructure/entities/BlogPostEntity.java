package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "blog_post")
public class BlogPostEntity extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", columnDefinition = "VARCHAR(200)")
    private String title;

    @Column(name = "body", columnDefinition = "Text")
    private String body;

    @Column(name = "category_blog", columnDefinition = "VARCHAR(200)")
    private String categoryBlog;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "author")
    private UserEntity author;

    @OneToMany(mappedBy = "blogPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CommentEntity> commentEntities = new HashSet<>();

    public void updateFromDTO(BlogPostDto dto) {
        this.title = dto.getTitle();
        this.body = dto.getBody();
        this.categoryBlog = dto.getCategoryBlog();
    }
}
