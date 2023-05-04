package hcmute.puzzle.infrastructure.dtos.olds;

import hcmute.puzzle.infrastructure.entities.Auditable;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
  private long id;

  @NotEmpty private String name;

  private String description;
  private String image;
  private String website;

  @Builder.Default
  private boolean isActive = false;

  private Long createdEmployerId;
}
