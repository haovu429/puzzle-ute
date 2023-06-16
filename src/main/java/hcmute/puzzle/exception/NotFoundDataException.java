package hcmute.puzzle.exception;

public class NotFoundDataException  extends CustomException {
    public NotFoundDataException(String message) {
        super(401, message);
    }

    public NotFoundDataException() {
        super(401, ErrorDefine.ServerError.NOT_FOUND_DATA_ERROR);
    }
}
