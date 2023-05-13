package hcmute.puzzle.services.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hcmute.puzzle.configuration.FreemarkerConfiguration;
import hcmute.puzzle.infrastructure.entities.TokenEntity;
import hcmute.puzzle.utils.mail.MailUtil;
import hcmute.puzzle.threads.ThreadService;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired FreemarkerConfiguration freeMarkerConfiguration;

  @Autowired ThreadService threadService1;

  public void executeSendMailWithThread(String receiveMail, String urlResetPass, TokenEntity token)
      throws InterruptedException,
          ExecutionException,
          MessagingException,
          TemplateException,
          IOException {

    // ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Boolean> sendMail =
        new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            try {
              sendMailForgotPwd(receiveMail, urlResetPass);
            } catch (Exception e) {
              logger.error(e.getMessage());
              return false;
            }
            return true;
          }
        };

    threadService1.execute(sendMail, ThreadService.MAIL_TASK, token);
    ExecutorService executorService = threadService1.executorService;
    if (executorService != null) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
      }
    }
  }

  public void executeSendMailVerifyAccountWithThread(String receiveMail, String verifyAccountUrl, TokenEntity token)
          throws InterruptedException,
                 ExecutionException,
                 MessagingException,
                 TemplateException,
                 IOException {

    // ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Boolean> sendMail =
            new Callable<Boolean>() {
              @Override
              public Boolean call() throws Exception {
                try {
                  sendMailVerifyAccount(receiveMail, verifyAccountUrl);
                } catch (Exception e) {
                  logger.error(e.getMessage());
                  return false;
                }
                return true;
              }
            };

    TokenEntity tokenEntity = token;
    ThreadService threadService = threadService1;
    threadService.execute(sendMail, ThreadService.MAIL_TASK, tokenEntity);
    ExecutorService executorService = threadService.executorService;
    if (executorService != null) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
      }
    }
  }


  public boolean sendMailForgotPwd(String receiveMail, String urlResetPass)
      throws MessagingException, IOException, TemplateException {

    MailUtil mailUtil = new MailUtil();
    MimeMessage mimeMessage = new MimeMessage(mailUtil.getSessionMail());
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

    //    helper.setTo(bo.getToEmails().parallelStream().toArray(String[]::new));
    //    helper.setBcc(bo.getBccEmails().parallelStream().toArray(String[]::new));
    //    helper.setCc(bo.getCcEmails().parallelStream().toArray(String[]::new));
    //    helper.setText(textBody, htmlBody);
    helper.setSubject("[PUZZLE] reset password");
    helper.setFrom(MailUtil.SYS_MAIL);
    helper.setTo(receiveMail);
    Map<String, Object> model = new HashMap<>();
    model.put("url", urlResetPass);
    freeMarkerConfiguration = new FreemarkerConfiguration();
    Configuration freeMakerConfig = freeMarkerConfiguration.freemarkerConfig().getConfiguration();
    Template temp = freeMakerConfig.getTemplate("forgot_password.html");
    // write the freemarker output to a StringWriter
    StringWriter stringWriter = new StringWriter();
    temp.process(model, stringWriter);

    // get the String from the StringWriter
    String html = stringWriter.toString();
    helper.setText(html, true);
    Transport.send(mimeMessage);
    // FreeMakerTemplateUtils

    return true;
  }

  public boolean sendMailVerifyAccount(String receiveMail, String verifyUrl)
          throws MessagingException, IOException, TemplateException {

    MailUtil mailUtil = new MailUtil();
    MimeMessage mimeMessage = new MimeMessage(mailUtil.getSessionMail());
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

    helper.setSubject("[PUZZLE] verify account");
    helper.setFrom(MailUtil.SYS_MAIL);
    helper.setTo(receiveMail);
    Map<String, Object> model = new HashMap<>();
    model.put("verify_url", verifyUrl);
    freeMarkerConfiguration = new FreemarkerConfiguration();
    Configuration freeMakerConfig = freeMarkerConfiguration.freemarkerConfig().getConfiguration();
    Template temp = freeMakerConfig.getTemplate("verify_account.html");
    // write the freemarker output to a StringWriter
    StringWriter stringWriter = new StringWriter();
    temp.process(model, stringWriter);

    // get the String from the StringWriter
    String html = stringWriter.toString();
    helper.setText(html, true);
    Transport.send(mimeMessage);
    // FreeMakerTemplateUtils
    return true;
  }


}
