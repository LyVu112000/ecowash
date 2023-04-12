package vuly.thesis.ecowash.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;


@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	private static String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);
			if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

				log.info("URI {}", request.getPathInfo());
				long userId = jwtTokenProvider.getUserIdFromJWT(jwt);
				UserPrincipal userDetails = (UserPrincipal) customUserDetailsService.loadUserByUserId(userId);
				Set<GrantedAuthority> grantedAuthoritySet = jwtTokenProvider.getGrantedAuthority(jwt, userDetails);
				userDetails.setAuthorities(grantedAuthoritySet);
				// FIXME: find the root cause of auto expire an account
//				if (userDetails.getUser().isAccountExpired()) {
//					throw new BadCredentialsException("Bad credentials");
//				}

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Could not set user authentication in security context", ex);
		}

		String zoneId = request.getHeader(DateTimeUtil.ZONE_ID_HEADER);
		response.addHeader(DateTimeUtil.ZONE_ID_HEADER, zoneId);

		filterChain.doFilter(request, response);
	}
}
