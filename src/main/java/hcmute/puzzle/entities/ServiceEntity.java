package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "service")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", columnDefinition = "VARCHAR(200)")
    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToMany(
            mappedBy = "serviceEntities",
            cascade = CascadeType.DETACH)
    private List<CandidateEntity> candidateEntities = new ArrayList<>();
}
