package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class CustomerUpdateRequest {
	@Size(max = 50)
	private String fullName;

	private String address;
	private String phoneNumber;
	private String note;
	private String logo;
	private String tax;
	@Email
	private String email;



}
