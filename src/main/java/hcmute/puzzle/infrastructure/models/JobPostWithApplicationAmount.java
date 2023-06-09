package hcmute.puzzle.infrastructure.models;

import hcmute.puzzle.infrastructure.dtos.response.JobPostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JobPostWithApplicationAmount {
	private JobPostDto jobPost;
	private Long applicationAmount;
}
