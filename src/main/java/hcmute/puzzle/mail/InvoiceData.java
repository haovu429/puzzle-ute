package hcmute.puzzle.mail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceData {
    String transactionId;
    String nameCustomer;
    String email;
    Date orderTime;
    String paymentMethod;
    List<InvoiceDetail> invoiceDetails = new ArrayList<>();
    double subtotal = 0;
    double tax = 0;
    double total = 0;

    public void calculatePrice() {
        long sub = 0;
        for (InvoiceDetail invoiceDetail : invoiceDetails) {
            invoiceDetail.setTotal(invoiceDetail.getPrice() * invoiceDetail.getQuantity());
            sub = sub + invoiceDetail.getTotal();
        }
        subtotal = (double) sub;
        //lam tron
        subtotal = Math.round(subtotal * 100.0) / 100.0;

        total = subtotal + tax;
        // lam tron
        total = Math.round(total * 100.0) / 100.0;

    }

}
