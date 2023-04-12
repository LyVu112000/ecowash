package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserUpdateRequest {
	@NotBlank
	@Size(min = 8, max = 20)
	private String username;
	private String roles;
	@Size(max = 50)
	private String fullName;
	private long customerId;
	@Email
	@Size(min = 5, max = 50)
	private String email;
	private String phoneNumber;
}
