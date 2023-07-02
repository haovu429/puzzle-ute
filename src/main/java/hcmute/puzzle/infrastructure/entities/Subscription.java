package hcmute.puzzle.infrastructure.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subscription")
public class Subscription extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "expiration_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

    @Column(name = "payment_transaction_code", columnDefinition = "VARCHAR(30)")
    private String paymentTransactionCode;

    @Column(name = "remaining_slot")
    private Integer remainingSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_user_email", referencedColumnName = "email")
    private User regUser; // Có thể tạo đăng ký gói trước

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private Package aPackage;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDelete = false;

//    @OneToOne(mappedBy = "subscribeEntity",, fetch = FetchType.LAZY)
//    private InvoiceEntity invoiceEntity;

    @Override
    public String toString() {
        return "SubscribeEntity{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", expirationTime=" + expirationTime +
                ", regUser=" + regUser.getEmail() +
                ", packageEntity=" + aPackage.getName() +
                ", invoiceEntity=" + paymentTransactionCode +
                '}';
    }
}
