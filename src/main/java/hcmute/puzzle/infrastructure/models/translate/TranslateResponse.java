package hcmute.puzzle.infrastructure.models.translate;


import lombok.Builder;
import lombok.Data;

@Data
public class TranslateResponse {
	private TranslateResponseObject[] data;
	private String message;
}
