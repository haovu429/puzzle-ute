package hcmute.puzzle.infrastructure.dtos.olds;

public class ResponseObject<T> {
  private String errorCode;
  private int status;
  private String message;
  private T data;

  public ResponseObject(int status, String message, T data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public ResponseObject(String errorCode, int status, String message) {
    this.errorCode = errorCode;
    this.status = status;
    this.message = message;
  }

  public ResponseObject(T data) {
    this.status = 200;
    this.errorCode = null;
    this.message = "Success";
    this.data = data;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
