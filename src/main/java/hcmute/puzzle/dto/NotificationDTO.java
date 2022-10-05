package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDTO {
    private long id;
    private String type;
    private String title;
    private String brief;
    private String time;
    private long userId;
    private long companyId;

}
