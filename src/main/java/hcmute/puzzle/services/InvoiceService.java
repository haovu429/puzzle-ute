package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;

import java.util.Date;

public interface InvoiceService {

    Invoice saveInvoice(Invoice invoice);

    DataResponse getInvoiceByEmailUser(String email);

    DataResponse getAllInvoice();

    DataResponse getAllInvoiceByTimeFrame(Date startTime, Date endTime);

    Invoice getOneInvoice(long invoiceId);

    long getTotalRevenue();

    long getTotalRevenue(Date startTime, Date endTime);
}
