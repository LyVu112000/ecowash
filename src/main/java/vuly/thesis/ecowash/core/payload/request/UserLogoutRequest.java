package vuly.thesis.ecowash.core.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Data
@Setter
public class UserLogoutRequest {
	private long userId;
}
