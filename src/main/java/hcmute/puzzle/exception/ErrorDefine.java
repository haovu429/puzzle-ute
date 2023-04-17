package hcmute.puzzle.exception;

public class Error {

    public class StorageError {
        public static final String STORAGE_ERROR = "storage_error";
        public static final String UPLOAD_FAILURE = "storage_error.upload_failure";
    }

    public class ServerError {
        public static final String SERVER_ERROR= "client_error.validate_error";
        public static final String CUSTOMER_ERROR = "client_error.not_found_error";
    }

    public class ClientError {
        public static final String VALIDATE_ERROR= "client_error.validate_error";
        public static final String NOT_FOUND_ERROR = "client_error.not_found_error";
        public static final String ALREADY_EXISTS_ERROR = "client_error.already_exist";
    }

    public class BatchExecuteError {
        public static final String PARTIAL_FAILURE = "batch_execute_error.partial_failure";
    }
}
