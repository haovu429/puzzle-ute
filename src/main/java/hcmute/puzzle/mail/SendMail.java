package hcmute.puzzle.mail;

//import com.sun.org.apache.xpath.internal.operations.Or;

import hcmute.puzzle.exception.CustomException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {

    public static void sendMail(MailObject mailObject) {

        // Recipient's email ID needs to be mentioned.

        if(mailObject.getReceiver() == null) {
            throw new CustomException("Reviver mail is null");
        }
        String to = mailObject.receiver;

        // Sender's email ID needs to be mentioned
        String from = "caihoncuagiamnguc@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        ///System.getenv("EMAIL");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                 //setup app password reference https://support.google.com/accounts/answer/185833?hl=en
                return new PasswordAuthentication(from, System.getenv("EMAIL_PASSWORD"));

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(mailObject.getSubject());

            // Now set the actual message
            message.setText(mailObject.getContent());
            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }
    public static void main(String[] args) {
        MailObject mailObject = new MailObject("haovu961@gmail.com", "hey", "I am Hao dep try");
        sendMail(mailObject);
    }

}
