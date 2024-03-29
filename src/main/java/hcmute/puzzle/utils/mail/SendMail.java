package hcmute.puzzle.utils.mail;

//import com.sun.org.apache.xpath.internal.operations.Or;

import hcmute.puzzle.exception.CustomException;

//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SendMail {

    @Value("${support.email}")
    String systemMail;

    @Value("${mail.service.pass}")
    String mailPassword;

    public static final String CONTENT_UTF8 = "utf-8";
    public static final String CONTENT_TYPE_TEXT_HTML = "html";
    public static final String CONTENT_TYPE_TEXT_PLAIN= "plain";

    public void sendMail(MailObject mailObject) {

        // Recipient's email ID needs to be mentioned.

        if(mailObject.getReceiver() == null) {
            throw new CustomException("Reviver mail is null");
        }
        String to = mailObject.receiver;

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                 //setup app password reference https://support.google.com/accounts/answer/185833?hl=en
                return new PasswordAuthentication(systemMail, mailPassword);

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(systemMail));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(mailObject.getSubject());

            String contentType = CONTENT_TYPE_TEXT_PLAIN;
            if (mailObject.getContentType() != null && mailObject.getContentType().equals(CONTENT_TYPE_TEXT_HTML)){
                contentType = CONTENT_TYPE_TEXT_HTML;
            }
            // Now set the actual message
            message.setText(mailObject.getContent(), CONTENT_UTF8, contentType);
            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }
    public void main(String[] args) {
        MailObject mailObject = new MailObject("haovu961@gmail.com", "hey", "I am Hao dep try", null);
        sendMail(mailObject);
    }

}
