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
    error = DataResponse.STATUS_CUSTOM_EXCEPTION;
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
}
