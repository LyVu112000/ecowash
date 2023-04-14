package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class StaffUpdateRequest {


	@Size(max = 50)
	private String fullName;
	private Long customerId;
	private long departmentId;
	@Email
	@Size(min = 5, max = 50)
	private String email;
	private String phoneNumber;
	private String note;
	private String roles;


}
