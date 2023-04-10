package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class DepartmentCreateRequest {

	@Size(max = 50)
	private String code;
	private String name;
	private String note;
	private String phoneNumber;

}
