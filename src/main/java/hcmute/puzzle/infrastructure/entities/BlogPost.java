package hcmute.puzzle.infrastructure.entities;

import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blog_post")
public class BlogPost extends Auditable{
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

    @JoinColumn(name = "blog_category_id")
    @ManyToOne()
    private BlogCategory blogCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private User author;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public void updateFromDTO(BlogPostDto dto) {
        this.title = dto.getTitle();
        this.body = dto.getBody();
        this.tags = dto.getTags();
        this.thumbnail = dto.getThumbnail();
    }
}
