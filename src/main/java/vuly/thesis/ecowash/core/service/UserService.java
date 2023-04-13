package vuly.thesis.ecowash.core.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.Role;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.entity.UserRefreshToken;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.payload.request.LoginRequest;
import vuly.thesis.ecowash.core.payload.request.RefreshTokenRequest;
import vuly.thesis.ecowash.core.payload.request.UserRequest;
import vuly.thesis.ecowash.core.payload.request.UserUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.LoginResponse;
import vuly.thesis.ecowash.core.payload.response.RefreshTokenResponse;
import vuly.thesis.ecowash.core.repository.core.RoleRepository;
import vuly.thesis.ecowash.core.repository.core.UserRefreshTokenRepository;
import vuly.thesis.ecowash.core.repository.core.UserRepository;
import vuly.thesis.ecowash.core.security.JwtProvider;
import vuly.thesis.ecowash.core.validation.UserValidation;

import java.util.*;

@Service
@Slf4j
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserValidation userValidation;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private JwtProvider tokenProvider;
	@Autowired
	private UserRefreshTokenRepository userRefreshTokenRepository;


	@Transactional
	public LoginResponse login(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()
				)
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User user = findByUsername(loginRequest.getUsername());
		String accessToken = tokenProvider.generateJwtToken(loginRequest.getUsername());
		String refreshToken = tokenProvider.generateJwtRefreshToken(loginRequest.getUsername());
		if(user.getRefreshToken() != null) {
			UserRefreshToken refreshTokenEntity = user.getRefreshToken();
			refreshTokenEntity.setToken(refreshToken);
			refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDate(refreshToken));
		} else {
			UserRefreshToken refreshTokenEntity = UserRefreshToken.builder()
					.token(refreshToken)
					.expiryDate(tokenProvider.getExpirationDate(refreshToken))
					.build();
			user.addRefreshToken(refreshTokenEntity);
		}
		return new LoginResponse().toBuilder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.username(user.getUsername())
				.role(user.getRole().getName())
				.build();
	}


	public RefreshTokenResponse getNewToken(RefreshTokenRequest request) {
		UserRefreshToken refreshTokenEntity = userRefreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(() -> new RuntimeException());
		User user = refreshTokenEntity.getUser();
		if (tokenProvider.isTokenExpired(refreshTokenEntity.getToken())) {
			refreshTokenEntity.setToken(tokenProvider.generateJwtRefreshToken(user.getEmail()));
			userRefreshTokenRepository.save(refreshTokenEntity);
		}
		return new RefreshTokenResponse().toBuilder()
				.accessToken(tokenProvider.generateJwtToken(user.getEmail()))
				.build();
	}

	@Transactional
	public void logout(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, 4012, null));
		log.info("Handling logout user");
		userRepository.logoutUser(user.getUsername());
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, 4012, null));
	}

    public User registerNewUserAccount(UserRequest request){
		User user = User
                .builder()
				.role(roleRepository.findByName(request.getRoles()).orElseThrow(() -> new AppException(4041)))
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
				.fullName(request.getFullName())
				.staffId(request.getStaffId())
				.isCustomer(request.getIsCustomer())
				.customerId(request.getCustomerId())
                .build();

        return user;
    }

	public User register(UserRequest request) {
		log.info("Handling register new user");
		if (userValidation.checkExistedUsername(request.getUsername())) {
			List<Object> params = new ArrayList();
			params.add(request.getUsername());
			throw new AppException(HttpStatus.BAD_REQUEST, 4010, params);
		}
		User user = registerNewUserAccount(request);
		return userRepository.save(user);
	}

	public void changeUserPassword(final User user, final String password) {
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
	}

	public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
		return passwordEncoder.matches(oldPassword, user.getPassword());
	}

	public User updateUserStaff(UserUpdateRequest request) {
		Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setRole(roleRepository.findByName(request.getRoles()).orElseThrow(() -> new AppException(4041)));
			user.setFullName(request.getFullName());
			user.setEmail(request.getEmail());
			user.setPhoneNumber(request.getPhoneNumber());
			user.setCustomerId(request.getCustomerId());
			userRepository.save(user);
		} else {
			throw new AppException(HttpStatus.BAD_REQUEST, 4000, null);
		}
		return null;
	}
}
