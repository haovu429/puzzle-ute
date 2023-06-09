package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.dtos.olds.InvoiceDto;
import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.models.enums.InvoiceStatus;
import hcmute.puzzle.infrastructure.repository.InvoiceRepository;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.services.InvoiceService;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
  @Autowired InvoiceRepository invoiceRepository;

  @Autowired Converter converter;

  public Invoice saveInvoice(Invoice invoice) {
    return invoiceRepository.save(invoice);
  }

  public DataResponse getInvoiceByEmailUser(String email) {
    List<InvoiceDto> invoiceDtos =
        invoiceRepository.findByEmail(email).stream()
            .map(invoiceEntity -> converter.toDTO(invoiceEntity))
            .collect(Collectors.toList());
    return new DataResponse(invoiceDtos);
  }

  public DataResponse getAllInvoice(){
    List<InvoiceDto> invoiceDtos =
            invoiceRepository.findAll().stream()
                    .map(invoiceEntity -> converter.toDTO(invoiceEntity))
                    .collect(Collectors.toList());
    return new DataResponse(invoiceDtos);
  }

  public DataResponse getAllInvoiceByTimeFrame(Date startTime, Date endTime){
    List<InvoiceDto> invoiceDtos =
            invoiceRepository.findAllByTimeFrame(startTime, endTime, InvoiceStatus.COMPLETED.getValue()).stream()
                    .map(invoiceEntity -> converter.toDTO(invoiceEntity))
                    .collect(Collectors.toList());
    return new DataResponse(invoiceDtos);
  }

  public Invoice getOneInvoice(long invoiceId) {
    Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
    if (invoice.isEmpty()) {
      throw new CustomException("Invoice not found");
    }
    return invoice.get();
  }

  public long getTotalRevenue() {
    return invoiceRepository.getTotalRevenue(InvoiceStatus.COMPLETED.getValue());
  }

  public long getTotalRevenue(Date startTime, Date endTime) {
    try {
      return invoiceRepository.getTotalRevenueByTimeFrame(startTime, endTime,InvoiceStatus.COMPLETED.getValue());
    } catch (AopInvocationException exception){
      exception.printStackTrace();
      return 0;
    }

  }
}
