package hcmute.puzzle.exception;


import org.apache.http.HttpStatus;

public enum ErrorResponse {

	ALREADY_EXISTS_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.ALREADY_EXISTS_ERROR, null),
	BAD_REQUEST_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.BAD_REQUEST_ERROR, null),
	INVALID_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.INVALID_ERROR, null),
	AUTHENTICATION_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.AUTHENTICATION_ERROR, null),
	UNAUTHORIZED_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.UNAUTHORIZED_ERROR, null),
	FILE_STORAGE_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.FILE_STORAGE_ERROR, null),
	INVALID_DATA_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.INVALID_DATA_ERROR, null),
	MISSING_PARAM_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.MISSING_PARAM_ERROR, null),
	NOT_FOUND_DATA_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.NOT_FOUND_DATA_ERROR, null),
	NOT_FOUND_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.NOT_FOUND_ERROR, null),
	PARTIAL_FAILURE_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.PARTIAL_FAILURE_ERROR, null),
	SERVER_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.SERVER_ERROR, null),
	CLIENT_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.CLIENT_ERROR, null),

	UPLOAD_FAILURE_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.UPLOAD_FAILURE_ERROR, null),

	VALIDATION_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorCode.VALIDATION_ERROR, null);

	private final int status;
	private final ErrorCode errorCode;
	private final String errorMsg;

	ErrorResponse(int status, ErrorCode errorCode, String errorMsg) {
		this.status = status;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public int getStatus() {
		return status;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
