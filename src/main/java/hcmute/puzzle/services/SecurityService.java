package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResetPassword;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.response.DataResponse;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

  DataResponse sendTokenForgotPwd(HttpServletRequest request, String email);

  DataResponse resetPassword(String token, String newPassword);
}
