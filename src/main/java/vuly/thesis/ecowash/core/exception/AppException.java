package vuly.thesis.ecowash.core.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class AppException extends RuntimeException {

	private HttpStatus status;
	private int code;
	private List<Object> params;

	public AppException(HttpStatus status, int code, List<Object> params) {
		this.code = code;
		this.status = status;
		this.params = params;
	}

	public AppException(int code) {
		this(HttpStatus.BAD_REQUEST, code, null);
	}

	public AppException(int code, Object... params) {
		this.code = code;
		this.status = HttpStatus.BAD_REQUEST;
		this.params = Arrays.asList(params);
	}
}
