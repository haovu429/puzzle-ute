package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "category")
public class Category extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", columnDefinition = "VARCHAR(200)")
    private String name;

//    @Column(name = "code", columnDefinition = "VARCHAR(10)")
//    private String code;

    @Column(name = "is_active")
    private boolean isActive;



}
