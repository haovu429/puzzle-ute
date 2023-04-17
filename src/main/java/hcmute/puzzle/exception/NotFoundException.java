package hcmute.puzzle.exception;

import hcmute.puzzle.response.DataResponse;

public class NotFoundException extends CustomException {
  public NotFoundException(String message) {
    super(404, message);
  }

  public NotFoundException() {
    super(404, ErrorDefine.ServerError.NOT_FOUND_ERROR);
  }
}
