package hcmute.puzzle.exception;

import hcmute.puzzle.response.DataResponse;

public class FileStorageException extends CustomException{

    public FileStorageException(String message) {
        super(414, message);
    }

    public FileStorageException() {
        super(414, ErrorDefine.ServerError.STORAGE_ERROR);
    }
}
