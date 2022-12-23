package hcmute.puzzle.services;

import hcmute.puzzle.dto.ResetPassword;
import hcmute.puzzle.dto.ResponseObject;
import hcmute.puzzle.response.DataResponse;

public interface SecurityService {

  DataResponse sendTokenForgotPwd(String email);

  DataResponse resetPassword(String token, String newPassword);
}
