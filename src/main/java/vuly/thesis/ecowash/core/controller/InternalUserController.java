package vuly.thesis.ecowash.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.request.UpdatePasswordRequest;
import vuly.thesis.ecowash.core.payload.request.UserRequest;
import vuly.thesis.ecowash.core.payload.request.UserUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.UserService;


@RestController
@Slf4j
@RequestMapping("/internal/user")
public class InternalUserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<Object> registerAccount(@RequestBody UserRequest request){
		Object result = userService.register(request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PutMapping("")
	public ResponseEntity<Object> updateUser(@RequestBody UserUpdateRequest request) {
		Object result = userService.updateUserStaff(request);
		return ResponseEntity.ok(AppResponse.success(result));
	}

	@PostMapping("/updatePassword")
	public ResponseEntity<Object> updatePW(@RequestBody UpdatePasswordRequest request) {
		final User user = userService.findByUsername(request.getUsername());

		if (!userService.checkIfValidOldPassword(user, request.getOldPassword())) {
			throw new AppException(HttpStatus.BAD_REQUEST, 4019, null);
		}

		userService.changeUserPassword(user, request.getNewPassword());
		return ResponseEntity.ok(AppResponse.success(user));
	}

}
