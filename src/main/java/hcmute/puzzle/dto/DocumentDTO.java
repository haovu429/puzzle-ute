package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocumentDTO {
    private long id;
    private String type;
    private String title;
    private String url;

    private long userId;
}
