package hcmute.puzzle.services;

import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.response.DataResponse;

public interface InvoiceService {

    InvoiceEntity saveInvoice(InvoiceEntity invoice);

    DataResponse getInvoiceByEmailUser(String email);

    DataResponse getAllInvoice();

    InvoiceEntity getOneInvoice(long invoiceId);

    long getTotalRevenue();
}
