package hcmute.puzzle.infrastructure.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "invoice")
public class Invoice extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(name = "phone", columnDefinition = "VARCHAR(20)")
    private String phone;

    @Column(name = "service_type", columnDefinition = "VARCHAR(30)")
    private String serviceType;

    @Column(name = "price")
    private long price; // giá bán

    @Column(name = "transaction_code", columnDefinition = "VARCHAR(30)")
    private String transactionCode;

    @Column(name = "pay_time")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date payTime;

    @Column(name = "payment_method", columnDefinition = "VARCHAR(30)")
    private String paymentMethod;

    @Column(name = "status", columnDefinition = "VARCHAR(30)")
    private String status;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDelete = false;

//    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
//    @JoinColumn(name = "subscribe_id")
//    private SubscribeEntity subscribeEntity;
}
