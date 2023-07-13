package hcmute.puzzle.exception;

public class AuthenticationException extends CustomException{

	public AuthenticationException(String message) {
		super(401, message);
	}

	public AuthenticationException() {
		super(401,ErrorDefine.ClientError.AUTHENTICATION_ERROR);
	}
}
