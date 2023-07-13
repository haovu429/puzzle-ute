package hcmute.puzzle.exception;

import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import jakarta.xml.bind.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

import static hcmute.puzzle.utils.Constant.ResponseCode.STATUS_CUSTOM_EXCEPTION;
import static hcmute.puzzle.utils.Constant.ResponseCode.STATUS_RUNTIME_EXCEPTION;
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
    DataResponse dataResponse = new DataResponse(ErrorResponse.SERVER_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
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
    DataResponse dataResponse = new DataResponse(ErrorResponse.UNAUTHORIZED_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
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
    DataResponse dataResponse = new DataResponse(ErrorResponse.SERVER_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(PartialFailureException.class)
  @ResponseBody
  public DataResponse handlePartialFailureException(PartialFailureException e) {
    log.error(e.getMessage(), e);
    error = e.getStatus();
    DataResponse dataResponse = new DataResponse(ErrorResponse.PARTIAL_FAILURE_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }


  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseBody
  public DataResponse handleMissingParams(MissingServletRequestParameterException e) {
    log.error(e.getMessage(), e);
    String name = e.getParameterName();
    error = STATUS_CUSTOM_EXCEPTION;
    return new DataResponse(String.valueOf(error), name + " parameter is missing", error);
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(BadRequestException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.BAD_REQUEST_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(AlreadyExistsException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(AlreadyExistsException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.ALREADY_EXISTS_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(FileStorageException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(FileStorageException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.FILE_STORAGE_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(InvalidDataException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(InvalidDataException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.INVALID_DATA_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(NotFoundDataException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(NotFoundDataException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.NOT_FOUND_DATA_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(NotFoundException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.NOT_FOUND_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(ServerException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(ServerException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.SERVER_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(UploadFailureException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(UploadFailureException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.UPLOAD_FAILURE_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(ValidationException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.VALIDATION_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  public DataResponse handleBadRequestException(MethodArgumentNotValidException e) {
    log.error(e.getMessage(), e);
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {

      String fieldName = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      errors.put(fieldName, message);
    });
    DataResponse dataResponse = new DataResponse<>(ErrorResponse.VALIDATION_ERROR);
    dataResponse.setErrMsg(errors.toString());
    return dataResponse;
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseBody
  public DataResponse handleAuthenticationException(AuthenticationException e) {
    log.error(e.getMessage(), e);
    DataResponse dataResponse = new DataResponse(ErrorResponse.AUTHENTICATION_ERROR);
    dataResponse.setErrMsg(e.getMessage());
    return dataResponse;
  }

}
