package vuly.thesis.ecowash.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vuly.thesis.ecowash.core.util.ExceptionBuilderUtil;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

	private ResponseEntity<Object> buildExceptionResponseEntity(
			AppExceptionResponse appExceptionResponse,
			HttpStatus status) {
		return new ResponseEntity<>(appExceptionResponse, status);
	}

	@ExceptionHandler(AppException.class)
	public ResponseEntity<?> handleInternalServer(AppException ex) {
		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(ex);
		return buildExceptionResponseEntity(appExceptionResponse, ex.getStatus());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<?> handleAuthorization(AuthenticationException authenticationException) {
		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.BAD_REQUEST, 4012, null));
		return buildExceptionResponseEntity(appExceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<?> handleLockedException(LockedException authenticationException) {
		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.BAD_REQUEST, 4020, null));
		return buildExceptionResponseEntity(appExceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		List<AppFieldError> appFieldErrors = new ArrayList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			appFieldErrors.add(
					AppFieldError.builder()
							.message(error.getDefaultMessage())
							.rejectValue(error.getRejectedValue())
							.field(error.getField())
							.object(error.getObjectName())
							.build());
		}

		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.BAD_REQUEST, 4000, null));
		appExceptionResponse.setAppFieldErrors(appFieldErrors);
		return buildExceptionResponseEntity(appExceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(
			NoHandlerFoundException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.NOT_FOUND, 4004, null));
		return buildExceptionResponseEntity(appExceptionResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({AccessDeniedException.class})
	public ResponseEntity<Object> handleAccessDeniedException() {
		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.FORBIDDEN, 4003, null));
		return buildExceptionResponseEntity(appExceptionResponse, HttpStatus.FORBIDDEN);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		List<Object> params = new ArrayList<>();
		params.add(ex.getLocalizedMessage().split("'")[1]);
		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.BAD_REQUEST, 4017, params));
		return buildExceptionResponseEntity(appExceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		AppExceptionResponse appExceptionResponse = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.BAD_REQUEST, 4000, null));
		appExceptionResponse.setMessage(ex.getMessage());
		log.info(ex.getMessage());
		return buildExceptionResponseEntity(appExceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AppExternalException.class)
	public ResponseEntity<?> handleExternalServer(AppExternalException ex) {
		String errorLine = AppExceptionParser.firstLine(ex);
		AppExternalExceptionResponse response = ExceptionBuilderUtil.exceptionResponseBuilder(ex);
		return ResponseEntity.ok(response);
	}
}
