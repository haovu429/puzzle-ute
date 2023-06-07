package hcmute.puzzle.exception;

public class ErrorDefine {

    //https://developer.mozilla.org/en-US/docs/Web/HTTP/Status

    public static final int SERVER_ERROR_CODE = 500;
    public static final int CLIENT_ERROR_CODE = 400;

    public class ServerError {
        public static final String SERVER_ERROR= "SERVER_ERROR";
        public static final String NOT_FOUND_ERROR = "NOT_FOUND_ERROR";

        public static final String NOT_FOUND_DATA_ERROR = "NOT_FOUND_DATA_ERROR";

        public static final String STORAGE_ERROR = "STORAGE_ERROR";
        public static final String UPLOAD_FAILURE = "UPLOAD_FAILURE ";

        public static final String VALIDATE_ERROR= "VALIDATE_ERROR";

        public static final String PARTIAL_FAILURE = "PARTIAL_FAILURE";
    }

    public class ClientError {
        public static final String AUTHENTICATION_ERROR= "AUTHENTICATION_ERROR";
        public static final String BAD_REQUEST_ERROR= "BAD_REQUEST_ERROR";
        public static final String INVALID_ERROR= "INVALID_ERROR";
        public static final String ALREADY_EXISTS_ERROR = "ALREADY_EXISTS_ERROR";

        public static final String UNAUTHORIZED_ERROR = "UNAUTHORIZED_ERROR";
    }

}
