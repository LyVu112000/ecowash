package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class TruckCreateRequest {

	@NotNull
	@Size(max = 50)
	private String code;

	private long staffId;

	public String getCode() {
		return code != null ? code.trim() : null;
	}
}
