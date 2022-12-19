package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.InvoiceDTO;
import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.repository.InvoiceRepository;
import hcmute.puzzle.response.DataResponse;
import hcmute.puzzle.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
  @Autowired InvoiceRepository invoiceRepository;

  @Autowired Converter converter;

  public InvoiceEntity saveInvoice(InvoiceEntity invoice) {
    return invoiceRepository.save(invoice);
  }

  public DataResponse getInvoiceByEmailUser(String email) {
    List<InvoiceDTO> invoiceDTOS =
        invoiceRepository.findByEmail(email).stream()
            .map(invoiceEntity -> converter.toDTO(invoiceEntity))
            .collect(Collectors.toList());
    return new DataResponse(invoiceDTOS);
  }
}
