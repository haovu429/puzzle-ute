package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExtraInfoDTO {
    private long id;
    private String name;
    private String type;
    private boolean isActive;
}
