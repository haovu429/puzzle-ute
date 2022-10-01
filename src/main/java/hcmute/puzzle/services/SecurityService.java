package hcmute.puzzle.services;


import hcmute.puzzle.dto.ResetPassword;
import hcmute.puzzle.dto.ResponseObject;

public interface SecurityService {

    ResponseObject sendTokenForgotPwd(String email);

    ResponseObject resetPassword(String token, ResetPassword resetPassword);
}
