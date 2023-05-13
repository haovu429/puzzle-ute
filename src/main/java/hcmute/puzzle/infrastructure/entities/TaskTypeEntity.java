package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Builder
@Entity
@Table(name = "md_task_type")
public class TaskTypeEntity extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", columnDefinition = "VARCHAR(100)")
    private String name;

    @Column(name = "type", columnDefinition = "VARCHAR(100)")
    private String type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}