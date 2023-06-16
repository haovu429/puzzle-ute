package hcmute.puzzle.exception;

public class UploadFailureException  extends Exception{

    public UploadFailureException(String message) {
        super(message);
    }

    public UploadFailureException() {
        super(ErrorDefine.ServerError.UPLOAD_FAILURE);
    }
}
