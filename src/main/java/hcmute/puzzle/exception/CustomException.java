package hcmute.puzzle.exception;

import lombok.*;

@Getter
@Setter
@Builder
public class CustomException extends RuntimeException {

  @Builder.Default
  private int status = 501;
  private String detail;

  public CustomException(String message, String detail) {
    super(message);
    this.detail = detail;
  }

  public CustomException( int status, String message, String detail) {
    super(message);
    this.detail = detail;
    this.status = status;
  }

  public CustomException(int status, String message) {
    super(message);
    this.status = status;
  }

  public CustomException(String message) {
    super(message);
  }

  CustomException() {}
}
