package hcmute.puzzle.firebase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PushNotificationResponse {
    private int status;
    private String message;
}
