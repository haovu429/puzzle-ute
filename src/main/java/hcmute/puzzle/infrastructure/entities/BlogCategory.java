package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Blog_category")
public class BlogCategory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", columnDefinition = "VARCHAR(200)", unique = true)
    private String name;

    //    @Column(name = "code", columnDefinition = "VARCHAR(10)")
    //    private String code;

    @Column(name = "is_active")
    private Boolean isActive;

}
