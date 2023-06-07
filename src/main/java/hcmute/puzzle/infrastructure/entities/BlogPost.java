package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blog_post")
public class BlogPostEntity extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", columnDefinition = "VARCHAR(200)")
    private String title;

    @Column(name = "thumbnail", columnDefinition = "TEXT")
    private String thumbnail;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "tags", columnDefinition = "VARCHAR(200)")
    private String tags;

    @JoinColumn(name = "category_blog_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private UserEntity author;

    @OneToMany(mappedBy = "blogPostEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentEntity> comments = new ArrayList<>();

    public void updateFromDTO(BlogPostDto dto) {
        this.title = dto.getTitle();
        this.body = dto.getBody();
        this.tags = dto.getTags();
        this.thumbnail = dto.getThumbnail();
    }
}
