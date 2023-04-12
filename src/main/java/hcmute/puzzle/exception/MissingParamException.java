package hcmute.puzzle.exception;

public class MissingParamException extends Exception{
    public MissingParamException() {}

    public MissingParamException(String message) {
        super(message);
    }
}
