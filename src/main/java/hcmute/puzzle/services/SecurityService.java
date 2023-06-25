package hcmute.puzzle.services;

import freemarker.template.TemplateException;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;

//import javax.mail.MessagingException;
import jakarta.mail.MessagingException;
//import javax.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface SecurityService {

  DataResponse sendTokenForgotPwd(HttpServletRequest request, String email) throws MessagingException, TemplateException, IOException, ExecutionException, InterruptedException;

  DataResponse sendTokenVerifyAccount(String email) throws MessagingException, TemplateException, IOException, ExecutionException, InterruptedException;

  DataResponse resetPassword(String token, String newPassword);

  DataResponse verifyAccount(String token);

}
