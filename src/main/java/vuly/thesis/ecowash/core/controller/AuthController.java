package vuly.thesis.ecowash.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.request.LoginRequest;
import vuly.thesis.ecowash.core.payload.request.RefreshTokenRequest;
import vuly.thesis.ecowash.core.payload.response.LoginResponse;
import vuly.thesis.ecowash.core.payload.response.RefreshTokenResponse;
import vuly.thesis.ecowash.core.service.UserService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private UserService userService;


	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		LoginResponse result = userService.login(loginRequest);
		return ResponseEntity.ok(result);
	}

//	@PostMapping("/logout")
//	public ResponseEntity<?> logout(@RequestBody UserLogoutRequest request) {
//		log.info("Internal request logout user");
//		userService.logout(request.getUserId());
//		log.info("Internal request logout user success");
//		return ResponseEntity.ok(AppResponse.success("Success"));
//	}

	@PostMapping("/token/refresh")
	public ResponseEntity<?> getNewToken(@RequestBody @Valid RefreshTokenRequest request) {
		RefreshTokenResponse response = userService.getNewToken(request);
		return ResponseEntity.ok(response);
	}
}
