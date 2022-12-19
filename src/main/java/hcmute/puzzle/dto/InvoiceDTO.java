package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class InvoiceDTO {
    private long id;
    private String email;
    private String phone;
    private String serviceType;
    private long price; // giá bán
    private String transactionCode;
    private Date payTime;
    private String paymentMethod;
    private String status;
}
