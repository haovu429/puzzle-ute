package hcmute.puzzle.infrastructure.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "category")
public class CategoryEntity extends Auditable{
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
