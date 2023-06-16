package hcmute.puzzle.infrastructure.dtos.request;

import hcmute.puzzle.infrastructure.models.Pagination;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
public class RequestPageable <T>{
	T body;
	//Pagination pagination;
}
