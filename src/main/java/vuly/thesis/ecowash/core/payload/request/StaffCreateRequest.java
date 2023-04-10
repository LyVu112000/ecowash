package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;
import vuly.thesis.ecowash.core.validation.ValidUsername;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class StaffCreateRequest {
	@NotNull
	@Size(max = 50)
	private String fullName;

	@NotNull
	@Size(max = 50)
	private String code;

	@ValidUsername
	private String username;
	private long departmentId;

	@NotNull
	@Email
	@Size(min = 5, max = 50)
	private String email;
	private String phoneNumber;
	private List<Long> customerId;
	private Boolean isCustomer;
	private String note;
	private String roles;

	public String getCode() {
		return code != null ? code.trim() : null;
	}
}
