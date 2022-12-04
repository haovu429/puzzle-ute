package hcmute.puzzle.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponse {

  public static final int STATUS_OK = 200;
  public static final int STATUS_NO_CONTENT = 200;
  public static final int STATUS_BAD = 400;
  public static final int STATUS_UNAUTHORIZED = 401;
  public static final int STATUS_FORBIDDEN = 403;
  public static final int STATUS_NOT_FOUND = 404;
  public static final int STATUS_NOT_AGAIN = 405;
  public static final int STATUS_RUNTIME_EXCEPTION = 500;
  public static final int STATUS_CUSTOM_EXCEPTION = 501;
  //public static final int STATUS_

  public static final String ERROR_NOT_FOUND = "404";
  public static final String ERROR_FORBIDDEN = "403";
  public static final String ERROR_NOT_AGAIN = "405";
  public static final String ERROR_INACTIVE = "406";

  private String errCode;
  private String errMsg;
  private int status;
  private Object data;

  public DataResponse(String errCode, String errMsg, int status) {
    this.errCode = errCode;
    this.errMsg = errMsg;
    this.status = status;
  }

  public DataResponse(Object data) {
    this.errCode = "200";
    this.status = STATUS_OK;
    this.data = data;
  }
}
