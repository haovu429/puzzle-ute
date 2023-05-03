package hcmute.puzzle.infrastructure.dtos.olds;

import hcmute.puzzle.infrastructure.entities.Auditable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class CompanyDto {
  private long id;

  @NotEmpty private String name;

  private String description;
  private String image;
  private String website;

  private boolean isActive = false;

  private Long createdEmployerId;
}
