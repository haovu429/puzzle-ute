package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.dtos.olds.InvoiceDto;
import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface InvoiceService {

    Invoice saveInvoice(Invoice invoice);

    List<InvoiceDto> getInvoiceByEmailUser(String email);

    Page<InvoiceDto> getAllInvoice(Pageable pageable);

    List<InvoiceDto> getAllInvoiceByTimeFrame(Date startTime, Date endTime);

    InvoiceDto getOneInvoice(long invoiceId);

    long getTotalRevenue();

    long getTotalRevenue(Date startTime, Date endTime);
}
