package hcmute.puzzle.exception;

import hcmute.puzzle.response.DataResponse;

public class NotFoundException extends Exception {
  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException() {
    super(DataResponse.MSG_ERROR_NOT_FOUND);
  }
}
