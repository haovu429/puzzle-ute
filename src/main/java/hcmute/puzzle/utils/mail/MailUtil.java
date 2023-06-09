package hcmute.puzzle.utils.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Slf4j
public class MailUtil {
  private static final String HOST = "smtp.gmail.com";

  private Session session;

  @Value("${mail.service.pass}")
  String mailPass;

  @Value("${support.email}")
  public String sysMail = "puzzleute@gmail.com";

  public Session getSessionMail() {

    if (session == null) {

      Properties props = new Properties();
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", HOST);
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.port", "465");
      props.put("mail.smtp.ssl.enable", "true");
      props.put("mail.smtp.connection-timeout", 1200);
      props.put("mail.smtp.timeout", 1000);

      session =
          Session.getDefaultInstance(
              props,
              new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(sysMail, "jfxlmaqxrjlreimo");
                }
              });
    }
    return session;
  }
}
