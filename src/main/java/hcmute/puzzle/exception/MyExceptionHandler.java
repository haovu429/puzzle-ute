package hcmute.puzzle.exception;

import hcmute.puzzle.response.DataResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// @ControllerAdvice
public class MyExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public DataResponse handleRuntimeException(RuntimeException e) {
    return new DataResponse("400", e.getMessage(), DataResponse.STATUS_OK);
  }

  @ExceptionHandler(CustomerException.class)
  @ResponseBody
  public DataResponse handleCustomerException(CustomerException e) {
    return new DataResponse("400", e.getMessage(), DataResponse.STATUS_OK);
  }
}
