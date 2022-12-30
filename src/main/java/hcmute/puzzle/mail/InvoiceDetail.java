package hcmute.puzzle.mail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceDetail {
    String itemName;
    long price;
    int quantity;
    long total;
}
