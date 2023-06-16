package hcmute.puzzle.exception;

public class BadRequestException extends CustomException{
    public BadRequestException() {super(400, ErrorDefine.ClientError.BAD_REQUEST_ERROR);}

    public BadRequestException(String message) {
        super(4400, message);
    }
}
