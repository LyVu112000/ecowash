package vuly.thesis.ecowash.core.util;

import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.exception.AppExceptionResponse;
import vuly.thesis.ecowash.core.exception.AppExternalException;
import vuly.thesis.ecowash.core.exception.AppExternalExceptionResponse;

@Component
public class ExceptionBuilderUtil {

	public static AppExceptionResponse exceptionResponseBuilder(AppException exception) {
		String errorMsg = MessageSourceUtil.getErrorMessage(exception.getCode(), exception.getParams());

		return AppExceptionResponse.builder()
				.message(errorMsg != null ? errorMsg : exception.getStatus().getReasonPhrase())
				.code(exception.getCode())
				.build();
	}

	public static AppExternalExceptionResponse exceptionResponseBuilder(AppExternalException exception) {
		return AppExternalExceptionResponse.builder()
				.code(exception.getCode())
				.message(exception.getMessage())
				.build();
	}
}
