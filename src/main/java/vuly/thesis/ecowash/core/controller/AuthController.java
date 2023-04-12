package vuly.thesis.ecowash.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.payload.request.LoginRequest;
import vuly.thesis.ecowash.core.payload.request.UserLogoutRequest;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.payload.response.LoginResponse;
import vuly.thesis.ecowash.core.security.JwtTokenProvider;
import vuly.thesis.ecowash.core.service.UserService;

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

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody UserLogoutRequest request) {
		log.info("Internal request logout user");
		userService.logout(request.getUserId());
		log.info("Internal request logout user success");
		return ResponseEntity.ok(AppResponse.success("Success"));
	}
}
