package hcmute.puzzle.utils.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailObject {
    String receiver;
    String subject;
    String content;
    String contentType;
}
