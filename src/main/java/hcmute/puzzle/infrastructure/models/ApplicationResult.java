package hcmute.puzzle.infrastructure.models;

import lombok.Data;

@Data
public class ApplicationResult {
	private String email;
	private Boolean result;
	private String note;
	private String subject;
	private Long jobPostId;
	private Long candidateId;
}
