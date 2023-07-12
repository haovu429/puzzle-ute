package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.dtos.olds.InvoiceDto;
import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.infrastructure.entities.SubComment;
import hcmute.puzzle.infrastructure.mappers.InvoiceMapper;
import hcmute.puzzle.infrastructure.models.enums.InvoiceStatus;
import hcmute.puzzle.infrastructure.repository.InvoiceRepository;
import hcmute.puzzle.services.InvoiceService;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
  @Autowired
  InvoiceRepository invoiceRepository;

  //  @Autowired Converter converter;
  @Autowired
  InvoiceMapper invoiceMapper;

  public Invoice saveInvoice(Invoice invoice) {
    return invoiceRepository.save(invoice);
  }

  public List<InvoiceDto> getInvoiceByEmailUser(String email) {
    List<InvoiceDto> invoiceDtos = invoiceRepository.findByEmail(email)
                                                    .stream()
                                                    .map(invoiceMapper::invoiceToInvoiceDto)
                                                    .sorted(Comparator.comparing(InvoiceDto::getPayTime).reversed())
                                                    .collect(Collectors.toList());
    return invoiceDtos;
  }

  public Page<InvoiceDto> getAllInvoice(Pageable pageable) {
    Page<InvoiceDto> invoiceDtos = invoiceRepository.findAll(pageable).map(invoiceMapper::invoiceToInvoiceDto);
    return invoiceDtos;
  }

  public List<InvoiceDto> getAllInvoiceByTimeFrame(Date startTime, Date endTime) {
    List<InvoiceDto> invoiceDtos = invoiceRepository.findAllByTimeFrame(startTime, endTime,
                                                                        InvoiceStatus.COMPLETED.getValue())
                                                    .stream()
                                                    .map(invoiceMapper::invoiceToInvoiceDto)
                                                    .collect(Collectors.toList());
    return invoiceDtos;
  }

  public InvoiceDto getOneInvoice(long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
                                       .orElseThrow(() -> new NotFoundDataException("Not found invoice"));
    return invoiceMapper.invoiceToInvoiceDto(invoice);
  }

  public long getTotalRevenue() {
    return invoiceRepository.getTotalRevenue(InvoiceStatus.COMPLETED.getValue());
  }

  public long getTotalRevenue(Date startTime, Date endTime) {
    try {
      return invoiceRepository.getTotalRevenueByTimeFrame(startTime, endTime, InvoiceStatus.COMPLETED.getValue());
    } catch (AopInvocationException exception) {
      exception.printStackTrace();
      return 0;
    }

  }
}
