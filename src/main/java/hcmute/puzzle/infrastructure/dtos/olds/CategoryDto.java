package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {
    private long id;
    private String name;
    private String image;
    private Boolean isActive;
}
