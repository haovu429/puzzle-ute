package hcmute.puzzle.infrastructure.mappers;

import hcmute.puzzle.infrastructure.dtos.olds.InvoiceDto;
import hcmute.puzzle.infrastructure.entities.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {EmployerMapper.class, CompanyMapper.class}, componentModel = "spring")
public interface InvoiceMapper {
	InvoiceMapper INSTANCE = Mappers.getMapper(InvoiceMapper.class);

	InvoiceDto invoiceToInvoiceDto(Invoice invoice);
}
