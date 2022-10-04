package hcmute.puzzle.dto;

public class ResponseObject {
  private String errorCode;
  private int status;
  private String message;
  private Object data;

  public ResponseObject(int status, String message, Object data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public ResponseObject(String errorCode, int status, String message) {
    this.errorCode = errorCode;
    this.status = status;
    this.message = message;
  }

  public ResponseObject(Object data) {
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

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
