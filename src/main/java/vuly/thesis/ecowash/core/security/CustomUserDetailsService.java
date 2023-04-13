package vuly.thesis.ecowash.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.repository.core.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username không tồn tại"));
        return UserDetailsDto.builder()
                .user(user)
                .userId(user.getId())
                .email(user.getEmail())
                .staffId(user.getStaffId())
                .role(user.getRole().getName())
                .build();
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getEmail())
//                .password(user.getPassword())
//                .authorities(new SimpleGrantedAuthority(user.getRole().getCode()))
//                .build();
    }

}
