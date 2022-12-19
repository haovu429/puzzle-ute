package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "invoice")
public class InvoiceEntity implements Serializable {
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

//    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
//    @JoinColumn(name = "subscribe_id")
//    private SubscribeEntity subscribeEntity;
}
