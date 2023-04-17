package hcmute.puzzle.exception;

public class InvalidException extends CustomException {
  public InvalidException() {super(412, ErrorDefine.ClientError.INVALID_ERROR);}

  public InvalidException(String message) {
    super(412, message);
  }
}
