package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {

	@NotNull
	@Size(max = 50)
	private String username;

	@NotNull
	@Size(min = 3, max = 20)
	private String password;
}
