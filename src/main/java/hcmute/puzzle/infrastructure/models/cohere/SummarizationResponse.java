package hcmute.puzzle.infrastructure.models.cohere;

import lombok.Data;

@Data
public class SummarizationResponse {
	private String id;
	private String summary;
	private Meta meta;
}
