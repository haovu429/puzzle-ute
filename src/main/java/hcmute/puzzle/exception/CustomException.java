package hcmute.puzzle.exception;

import lombok.Builder;

@Builder
public class CustomException extends RuntimeException {

  public CustomException(String message) {
    super(message);
  }

  CustomException() {}
}
