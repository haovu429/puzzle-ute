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

    @JoinColumn(name = "category_blog_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private User author;

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
