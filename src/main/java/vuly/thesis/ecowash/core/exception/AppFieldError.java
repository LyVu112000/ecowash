package vuly.thesis.ecowash.core.exception;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppFieldError {

	private String object;
	private String field;
	private Object rejectValue;
	private String message;
}
