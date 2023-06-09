package hcmute.puzzle.infrastructure.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ResponseApplication {
    Long candidateId;
    Long jobPostId;
    Long applicationId;
    String email;
    String subject;
    boolean result = true;
    String note;
}
