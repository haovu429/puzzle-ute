package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDTO {
    private long id;
    private String result;
    private String note;
    private long jobPostId;
    private long candidateId;
}
