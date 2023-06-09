package hcmute.puzzle.exception;

import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static hcmute.puzzle.utils.Constant.ResponseCode.*;
import static hcmute.puzzle.utils.Constant.ResponseMessage.MSG_ERROR_SERVER;

@Slf4j
@ControllerAdvice
public class MyExceptionHandler {

  @Value("${spring.servlet.multipart.max-file-size}")
  String fileSizeLimit;

  int error;

  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public DataResponse handleRuntimeException(RuntimeException e) {
    log.error(e.getMessage(), e);
    return new DataResponse(MSG_ERROR_SERVER, e.getMessage(), STATUS_RUNTIME_EXCEPTION);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseBody
  public DataResponse handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    log.error(e.getMessage(), e);

    String mess = String.format("File size exceeds the limit (%s)", fileSizeLimit);
    return new DataResponse(MSG_ERROR_SERVER, mess , STATUS_RUNTIME_EXCEPTION);
  }

  @ExceptionHandler(UnauthorizedException.class)
  @ResponseBody
  public DataResponse handleRuntimeException(UnauthorizedException e) {
    log.error(e.getMessage(), e);
    return new DataResponse("401", e.getMessage(), STATUS_UNAUTHORIZED);
  }


  @ExceptionHandler(CustomException.class)
  @ResponseBody
  public DataResponse handleCustomerException(CustomException e) {
    log.error(e.getMessage(), e);
    return new DataResponse(
            e.getMessage(), e.getDetail(), e.getStatus());
  }

  @ExceptionHandler(NullPointerException.class)
  @ResponseBody
  public DataResponse handleNullPointerException(NullPointerException e) {
    log.error(e.getMessage(), e);
    return new DataResponse(
             "500", e.getMessage(), 500);
  }

  @ExceptionHandler(PartialFailureException.class)
  @ResponseBody
  public DataResponse handlePartialFailureException(PartialFailureException e) {
    log.error(e.getMessage(), e);
    error = e.getStatus();
    return new DataResponse(String.valueOf(error), e.getMessage(), error);
  }


  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseBody
  public DataResponse handleMissingParams(MissingServletRequestParameterException e) {
    log.error(e.getMessage(), e);
    String name = e.getParameterName();
    // System.out.println(name + " parameter is missing");
    // Actual exception handling
    error = STATUS_CUSTOM_EXCEPTION;
    return new DataResponse(String.valueOf(error), name + " parameter is missing", error);
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(BadRequestException e) {
    log.error(e.getMessage(), e);
    return new DataResponse(
            ErrorDefine.ClientError.BAD_REQUEST_ERROR, e.getDetail(), 400);
  }

}
