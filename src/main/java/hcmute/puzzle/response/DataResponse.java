package hcmute.puzzle.response;

import lombok.*;

@Data
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
  public static final String MSG_CUSTOM_EXCEPTION = "CUSTOM_EXCEPTION";
  //public static final int STATUS_

  public static final String CODE_ERROR_NOT_FOUND = "404";
  public static final String MSG_ERROR_NOT_FOUND = "ERROR_NOT_FOUND";
  public static final String CODE_ERROR_FORBIDDEN = "403";
  public static final String MSG_ERROR_FORBIDDEN = "ERROR_FORBIDDEN";
  public static final String CODE_ERROR_NOT_AGAIN = "405";
  public static final String MSG_ERROR_NOT_AGAIN = "ERROR_NOT_AGAIN";
  public static final String CODE_ERROR_INACTIVE = "406";
  public static final String MSG_ERROR_INACTIVE = "ERROR_INACTIVE";

  public static final String MSG_ERROR_SERVER = "ERROR_SERVER";
  public static final String MSG_ERROR_CLIENT = "ERROR_CLIENT";

  public static final String MSG_ERROR_MISSING_PARAMS = "ERROR_MISSING_PARAMS";


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
    this.errCode = "";
    this.status = STATUS_OK;
    this.data = data;
  }
}
