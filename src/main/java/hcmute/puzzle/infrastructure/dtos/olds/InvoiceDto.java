package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class InvoiceDto {
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
