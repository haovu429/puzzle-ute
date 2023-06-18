package hcmute.puzzle.hirize.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParserRequestPayload {
	String payload;
	String file_name;
}
