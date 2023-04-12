package vuly.thesis.ecowash.core.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.util.StringUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UserPrincipal implements UserDetails {
	private User user;
	private static final ObjectMapper mapper = new ObjectMapper();

	public UserPrincipal(User user) {
		this.user = user;
	}
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Map<String, List<String>>> resourceAccess;
	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getName(){
		return user.getFullName();
	}


	public boolean isGranted(String role) {
		if (authorities.isEmpty()) {
			return false;
		}

		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().equals(role)) {
				return true;
			}
		}
		return false;
	}

	public String description() {
		Map<String, String> result = new HashMap<>();
		result.put("userId", getUsername());
		if(!StringUtil.isEmpty(getName())){
			result.put("name", getName());
		}
		try {
			return mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			log.error("USER DESCRIPTION", e);
		}

		return toString();
	}
}
