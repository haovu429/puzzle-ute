package hcmute.puzzle.exception;

public class AlreadyExistsException extends CustomException{

    public AlreadyExistsException(String message) {
        super(409, message);
    }

    public AlreadyExistsException() {
        super(409,ErrorDefine.ClientError.ALREADY_EXISTS_ERROR);
    }
}
