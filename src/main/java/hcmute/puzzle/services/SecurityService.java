package hcmute.puzzle.services;

import freemarker.template.TemplateException;
import hcmute.puzzle.dto.ResetPassword;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.entities.TokenEntity;
import hcmute.puzzle.response.DataResponse;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface SecurityService {

  DataResponse sendTokenForgotPwd(HttpServletRequest request, String email) throws MessagingException, TemplateException, IOException, ExecutionException, InterruptedException;

  DataResponse resetPassword(String token, String newPassword);
}
