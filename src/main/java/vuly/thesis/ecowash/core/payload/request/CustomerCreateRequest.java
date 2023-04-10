package vuly.thesis.ecowash.core.payload.request;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
public class CustomerCreateRequest {
	@Size(max = 50)
	private String fullName;

	@NotNull
	@Size(max = 50)
	private String code;
	private String address;
	private String phoneNumber;
	private String note;
	private String logo;
	@Email
	private String email;
	private String tax;





	public String getCode() {
		return code != null ? code.trim() : null;
	}
}
