package hcmute.puzzle.infrastructure.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseApplication {
    long candidateId = -1;
    long jobPostId = -1;
    long applicationId = -1;
    String email;
    String subject;
    boolean result = true;
    String note;
}
