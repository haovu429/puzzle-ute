package hcmute.puzzle.dto;

import hcmute.puzzle.entities.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BlogPostDTO {
    private long id;
    private String title;
    private String body;
    private Date createTime;
    private Date lastUpdate;
    private String categoryBlog;
    private long userId;
}
