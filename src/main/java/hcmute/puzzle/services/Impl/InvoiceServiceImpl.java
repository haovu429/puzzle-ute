package hcmute.puzzle.services.Impl;

import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.repository.InvoiceRepository;
import hcmute.puzzle.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    InvoiceRepository invoiceRepository;

    public InvoiceEntity saveInvoice(InvoiceEntity invoice) {
        return invoiceRepository.save(invoice);
    }
}
