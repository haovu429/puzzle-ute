package hcmute.puzzle.services.Impl;

import hcmute.puzzle.converter.Converter;
import hcmute.puzzle.dto.InvoiceDTO;
import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.model.enums.InvoiceStatus;
import hcmute.puzzle.repository.InvoiceRepository;
import hcmute.puzzle.response.DataResponse;
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

  public DataResponse getAllInvoice(){
    List<InvoiceDTO> invoiceDTOS =
            invoiceRepository.findAll().stream()
                    .map(invoiceEntity -> converter.toDTO(invoiceEntity))
                    .collect(Collectors.toList());
    return new DataResponse(invoiceDTOS);
  }

  public DataResponse getAllInvoiceByTimeFrame(Date startTime, Date endTime){
    List<InvoiceDTO> invoiceDTOS =
            invoiceRepository.findAllByTimeFrame(startTime, endTime, InvoiceStatus.COMPLETED.getValue()).stream()
                    .map(invoiceEntity -> converter.toDTO(invoiceEntity))
                    .collect(Collectors.toList());
    return new DataResponse(invoiceDTOS);
  }

  public InvoiceEntity getOneInvoice(long invoiceId) {
    Optional<InvoiceEntity> invoice = invoiceRepository.findById(invoiceId);
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
