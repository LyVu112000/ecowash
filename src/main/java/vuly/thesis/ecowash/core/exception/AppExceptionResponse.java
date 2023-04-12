package vuly.thesis.ecowash.core.exception;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppExceptionResponse {

	private String message;
	private int code;
	private List<AppFieldError> appFieldErrors = Collections.emptyList();
}
