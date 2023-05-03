package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SubscribeDto {
    private long id;
    private Date startTime;
    private Date expirationTime;
    private String transactionCode;

}
