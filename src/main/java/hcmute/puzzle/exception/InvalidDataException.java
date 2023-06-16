package hcmute.puzzle.exception;

public class InvalidDataException extends CustomException {
  public InvalidDataException() {super(412, ErrorDefine.ClientError.INVALID_ERROR);}

  public InvalidDataException(String message) {
    super(412, message);
  }
}
