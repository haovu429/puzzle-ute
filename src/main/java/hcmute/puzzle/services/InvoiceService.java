package hcmute.puzzle.services;

import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.response.DataResponse;

import java.util.Date;

public interface InvoiceService {

    InvoiceEntity saveInvoice(InvoiceEntity invoice);

    DataResponse getInvoiceByEmailUser(String email);

    DataResponse getAllInvoice();

    DataResponse getAllInvoiceByTimeFrame(Date startTime, Date endTime);

    InvoiceEntity getOneInvoice(long invoiceId);

    long getTotalRevenue();

    long getTotalRevenue(Date startTime, Date endTime);
}
