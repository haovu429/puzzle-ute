package hcmute.puzzle.exception;

import hcmute.puzzle.response.DataResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class MyExceptionHandler {

  int error;

  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public DataResponse handleRuntimeException(RuntimeException e) {
    return new DataResponse(
        DataResponse.MSG_ERROR_SERVER, e.getMessage(), DataResponse.STATUS_RUNTIME_EXCEPTION);
  }

  @ExceptionHandler(CustomException.class)
  @ResponseBody
  public DataResponse handleCustomerException(CustomException e) {
    return new DataResponse(
            ErrorDefine.ServerError.SERVER_ERROR, e.getDetail(), 500);
  }

  @ExceptionHandler(PartialFailureException.class)
  @ResponseBody
  public DataResponse handlePartialFailureException(PartialFailureException e) {
    error = e.getStatus();
    return new DataResponse(String.valueOf(error), e.getMessage(), error);
  }


  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseBody
  public DataResponse handleMissingParams(MissingServletRequestParameterException ex) {
    String name = ex.getParameterName();
    // System.out.println(name + " parameter is missing");
    // Actual exception handling
    error = DataResponse.STATUS_CUSTOM_EXCEPTION;
    return new DataResponse(String.valueOf(error), name + " parameter is missing", error);
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(BadRequestException e) {
    return new DataResponse(
            ErrorDefine.ClientError.BAD_REQUEST_ERROR, e.getDetail(), 400);
  }

}
