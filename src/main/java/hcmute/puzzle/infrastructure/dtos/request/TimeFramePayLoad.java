package hcmute.puzzle.infrastructure.dtos.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TimeFramePayLoad {
    private Date startTime;
    private Date endTime;
}
