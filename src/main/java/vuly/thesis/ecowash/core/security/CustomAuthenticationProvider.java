package vuly.thesis.ecowash.core.security;


import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.core.UserRepository;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        String[] username = auth.getName().split(":");
        User user = userRepository.findByUsername(username[1])
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, 4012, null));;
        if ((user == null)) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if (user.isUsing2FA()) {
            String verificationCode
                    = ((CustomWebAuthenticationDetails) auth.getDetails())
                    .getVerificationCode();
            Totp totp = new Totp(user.getSecret());
            if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                throw new BadCredentialsException("Invalid verfication code");
            }
        }

        Authentication result = super.authenticate(auth);
        return new UsernamePasswordAuthenticationToken(
                user, result.getCredentials(), result.getAuthorities());
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
