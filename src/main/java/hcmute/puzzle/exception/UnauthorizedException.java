package hcmute.puzzle.exception;

public class UnauthorizedException extends CustomException{
    public UnauthorizedException(String message) {
        super(401, message);
    }

    public UnauthorizedException() {
        super(401,ErrorDefine.ClientError.UNAUTHORIZED_ERROR);
    }

}
