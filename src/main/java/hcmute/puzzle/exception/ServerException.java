package hcmute.puzzle.exception;

public class ServerException  extends CustomException {
    public ServerException(String message) {
        super(500, message);
    }

    public ServerException() {
        super(500, ErrorDefine.ServerError.SERVER_ERROR);
    }
}
