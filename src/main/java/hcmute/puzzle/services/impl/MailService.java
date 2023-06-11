package hcmute.puzzle.services.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hcmute.puzzle.configuration.FreemarkerConfiguration;
import hcmute.puzzle.infrastructure.entities.Token;
import hcmute.puzzle.threads.ThreadService;
import hcmute.puzzle.utils.Constant;
import hcmute.puzzle.utils.mail.MailUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import javax.mail.MessagingException;
//import javax.mail.Transport;
//import javax.mail.internet.MimeMessage;

//import jakarta.mail.MessagingException;
//import jakarta.mail.Transport;
//import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  FreemarkerConfiguration freeMarkerConfiguration;

  @Autowired
  ThreadService threadService1;

  @Autowired
  Environment environment;

  @Transactional
  public void executeSendMailWithThread(String receiveMail, String urlResetPass, Token token) throws
          InterruptedException, ExecutionException, MessagingException, TemplateException, IOException {

    // ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Boolean> sendMail = new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try {
          sendMailForgotPwd(receiveMail, urlResetPass);
        } catch (Exception e) {
          logger.error(e.getMessage());
          throw e;
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

  @Transactional
  public void executeSendMailVerifyAccountWithThread(String receiveMail, String verifyAccountUrl, Token token) throws
          InterruptedException, ExecutionException, MessagingException, TemplateException, IOException {

    // ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Boolean> sendMail = new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try {
          sendMailVerifyAccount(receiveMail, verifyAccountUrl);
        } catch (Exception e) {
          logger.error(e.getMessage());
          throw e;
        }
        return true;
      }
    };

    Token tokenEntity = token;
    ThreadService threadService = threadService1;
    threadService.execute(sendMail, ThreadService.MAIL_TASK, tokenEntity);
    ExecutorService executorService = threadService.executorService;
    if (executorService != null) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
      }
    }
  }


  @Transactional
  public boolean sendMailForgotPwd(String receiveMail, String urlResetPass) throws MessagingException, IOException,
          TemplateException {

    MailUtil mailUtil = new MailUtil();
    MimeMessage mimeMessage = new MimeMessage(mailUtil.getSessionMail());
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

    //    helper.setTo(bo.getToEmails().parallelStream().toArray(String[]::new));
    //    helper.setBcc(bo.getBccEmails().parallelStream().toArray(String[]::new));
    //    helper.setCc(bo.getCcEmails().parallelStream().toArray(String[]::new));
    //    helper.setText(textBody, htmlBody);
    helper.setSubject("[PUZZLE] reset password");
    helper.setFrom(Objects.nonNull(environment.getProperty("support.email")) ? Objects.requireNonNull(
            environment.getProperty("support.email")) : Constant.SYSTEM_MAIL);
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
    helper.setFrom(Objects.nonNull(environment.getProperty("support.email")) ? Objects.requireNonNull(
            environment.getProperty("support.email")) : Constant.SYSTEM_MAIL);
    helper.setTo(receiveMail);
    Map<String, Object> model = new HashMap<>();
    model.put("verify_url", verifyUrl);
    model.put("action", "verify account");
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
