package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

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

}
