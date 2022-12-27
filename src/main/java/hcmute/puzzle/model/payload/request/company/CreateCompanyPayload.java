package hcmute.puzzle.model.payload.request.company;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCompanyPayload {
    private String name;
    private String description;
    private String image;
    private String website;
}
