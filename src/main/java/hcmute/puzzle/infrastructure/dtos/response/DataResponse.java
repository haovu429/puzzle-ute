package hcmute.puzzle.infrastructure.dtos.response;

import hcmute.puzzle.exception.ErrorCode;
import hcmute.puzzle.exception.ErrorResponse;
import lombok.*;

import static hcmute.puzzle.utils.Constant.ResponseCode.STATUS_OK;

//@Builder

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse<T> {
  private String errCode;
  private String errMsg;
  private int status;
  private T data;

  public DataResponse(String errCode, String errMsg, int status) {
    this.errCode = errCode;
    this.errMsg = errMsg;
    this.status = status;
  }

  public DataResponse(ErrorResponse errorResponse) {
    this.errCode = errorResponse.getErrorCode().getValue();
    this.errMsg = errorResponse.getErrorMsg();
    this.status = errorResponse.getStatus();
  }

  public DataResponse(T data) {
    this.errCode = null;
    this.status = STATUS_OK;
    this.data = data;
  }
}
