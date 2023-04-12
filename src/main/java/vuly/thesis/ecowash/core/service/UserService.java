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
import vuly.thesis.ecowash.core.payload.request.UserRequest;
import vuly.thesis.ecowash.core.payload.request.UserUpdateRequest;
import vuly.thesis.ecowash.core.payload.response.LoginResponse;
import vuly.thesis.ecowash.core.repository.core.RoleRepository;
import vuly.thesis.ecowash.core.repository.core.UserRefreshTokenRepository;
import vuly.thesis.ecowash.core.repository.core.UserRepository;
import vuly.thesis.ecowash.core.security.JwtTokenProvider;
import vuly.thesis.ecowash.core.validation.UserValidation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	private JwtTokenProvider tokenProvider;
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
		UserRefreshToken userRefreshToken = createRefreshToken(user);
		String accessToken = tokenProvider.generateToken(user, userRefreshToken.getJti());
		String exchangeToken = tokenProvider.generateExchangeToken(user.getId());
		List<String> roleName = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
		String role = String.join(",", roleName);
		return new LoginResponse(accessToken,userRefreshToken.getToken(), exchangeToken, user, role);
	}


	private UserRefreshToken createRefreshToken(User user) {
		String token = RandomStringUtils.randomAlphanumeric(128);
		return userRefreshTokenRepository.save(new UserRefreshToken(token, user));
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
		Set<Role> roles = new HashSet<>();
		if (request.getRoles() != null) {
			String[] rolesArray = request.getRoles().split(",");

			Stream.of(rolesArray).forEach(role -> {
				if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
					roles.add(roleRepository.findByName(role));
				}
			});
		}
		User user = User
                .builder()
                .roles(roles)
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
			Set<Role> roles = new HashSet<>();
			if (request.getRoles() != null) {
				String[] rolesArray = request.getRoles().split(",");

				Stream.of(rolesArray).forEach(roleName -> {
					Role role = roleRepository.findByName(roleName);
					roles.add(role);
				});
			}
			user.setRoles(roles);
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
