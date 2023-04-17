package hcmute.puzzle.exception;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomError {
    private int status;
    private String name;
    private String msg;
}
