package hcmute.puzzle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subscribe")
public class SubscribeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "expiration_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "reg_user_email", referencedColumnName = "email")
    private UserEntity regUser; // Có thể tạo đăng ký gói trước

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "package_id", nullable = false)
    private PackageEntity packageEntity;

//    @OneToOne(mappedBy = "subscribeEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
//    private InvoiceEntity invoiceEntity;

    @Column(name = "payment_transaction_code", columnDefinition = "VARCHAR(30)")
    private String paymentTransactionCode;

    @Override
    public String toString() {
        return "SubscribeEntity{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", expirationTime=" + expirationTime +
                ", regUser=" + regUser.getEmail() +
                ", packageEntity=" + packageEntity.getName() +
                ", invoiceEntity=" + paymentTransactionCode +
                '}';
    }
}
