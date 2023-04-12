package vuly.thesis.ecowash.core.payload.response;

import lombok.Getter;
import lombok.Setter;
import vuly.thesis.ecowash.core.entity.User;

@Getter
@Setter
public class LoginResponse {
	private final User user;
	private String accessToken;
	private int code = 2000;
	private String refreshToken;
	private String tokenType = "Bearer";
	private String exchangeToken;
	private String role;
	public LoginResponse(String accessToken, String refreshToken, String exchangeToken, User user, String role) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.user = user;
		this.exchangeToken = exchangeToken;
		this.role = role;
	}
}
