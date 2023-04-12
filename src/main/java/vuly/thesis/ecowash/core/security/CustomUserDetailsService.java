package vuly.thesis.ecowash.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.repository.core.UserRepository;

import java.util.HashSet;
import java.util.Set;


@Service
@Slf4j
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String[] orgAndUsername = username.split(":");
		User user = userRepository.findByUsername(orgAndUsername[1])
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		return new UserPrincipal(user);
	}


	private Set<GrantedAuthority> getGrantedAuthorities(Set<String> roles) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}

	public UserDetails loadUserByUserId(long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userId));

		return new UserPrincipal(user);
	}
}
