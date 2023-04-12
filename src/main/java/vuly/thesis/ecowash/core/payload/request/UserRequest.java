package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vuly.thesis.ecowash.core.validation.ValidEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Data
@Setter
public class UserRequest {
	@NotBlank
	@Size(min = 3, max = 30)
	private String username;
	private String password;
	@NotNull
	@Size(min = 1)
	private String matchingPassword;
	@NotBlank
	@Size(min = 10, max = 12)
	private String phoneNumber;
	@ValidEmail
	@NotNull
	@Size(min = 1)
	private String email;
	@Size(min = 5, max = 100)
	private String fullName;
	private String roles;
	private long staffId;
	private long customerId;
	private Boolean isCustomer;
}
