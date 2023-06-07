package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "package")
public class PackageEntity extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;

    @Column(name = "code", columnDefinition = "VARCHAR(10)", nullable = false, unique = true)
    private String code;

    @Transient
    private Long price; // giá bán, không lưu vào db

    @Column(name = "cost", nullable = false)
    private long cost; // giá gốc

    @Column(name = "duration", nullable = false)
    private long duration; // tính theo giây

    @Column(name = "num_of_job_post", nullable = false)
    private int numOfJobPost; // só job post mua thêm

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "service_type", columnDefinition = "VARCHAR(20)", nullable = false)
    private String serviceType;

    @Column(name = "public_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicTime;

    @Column(name = "for_user_type", columnDefinition = "VARCHAR(50)")
    private String forUserType;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDelete = false;

    @Builder.Default
    @OneToMany(mappedBy = "packageEntity", fetch = FetchType.LAZY)
    private List<SubscriptionEntity> subscribeEntities = new ArrayList<>(); // Khi add, phải add từ 2 phía
}
