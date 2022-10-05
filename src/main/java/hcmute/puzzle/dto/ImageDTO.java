package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageDTO {
    private long id;
    private String type;
    private String title;
    private String url;
    private long codeId;
}
