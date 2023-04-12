package vuly.thesis.ecowash.core.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.Sets;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vuly.thesis.ecowash.core.entity.User;
import vuly.thesis.ecowash.core.payload.dto.ExchangeTokenUserDto;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
	final JsonNodeFactory factory = JsonNodeFactory.instance;
	@Value("${security.jwt.secret}")
	private String secretKey;

	@Value("${security.jwt.secret_kid}")
	private String secretKeyId;

	@Value("${security.jwt.legacy_secret}")
	private String legacySecretKey;

	@Value("${security.jwt.legacy_secret_kid}")
	private String legacySecretKeyId;

	@Value("${security.jwt.expire-time}")
	private int jwtExpirationInSecond;

	@Value("${security.jwt.exchange-token-expire-time}")
	private int jwtExchangeTokenExpirationInSecond;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
		legacySecretKey = Base64.getEncoder().encodeToString(legacySecretKey.getBytes());
		log.info(">>>> jwtExpirationInSecond " + jwtExpirationInSecond);
	}
	public String generateToken(User user, String jti) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInSecond*1000);
		log.info("START PROVIDE TOKEN to user " + user.getUsername() + " expiration at " + expiryDate + " gen time " + now);
		Map<String, Object> header = new HashMap<>();
		header.put("kid", secretKeyId);

		Claims claims = Jwts.claims().setSubject(user.getId().toString());

		String roles = user.getRoles().stream().map(role -> role.getName())
				.collect(Collectors.joining(","));

		claims.put("roles",roles);
		claims.put("name",user.getFullName());
		claims.put("username",user.getUsername());
		claims.put("jti",jti);
		claims.put("staff_id",user.getStaffId());
		claims.put("customer_id",user.getCustomerId());
		return Jwts.builder().setHeaderParams(header)
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}

	public String generateExchangeToken(Long uid) {
		Map<String, Object> header = new HashMap<>();
		header.put("kid", secretKeyId);
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExchangeTokenExpirationInSecond*1000);
		Claims claims = Jwts.claims().setSubject(uid.toString());
		claims.put("uid", uid);

		// end of dynamic Role
		return Jwts.builder()
				.setHeaderParams(header)
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}

	public long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(secretByTokenKid(token))
				.parseClaimsJws(token)
				.getBody();

		return Long.valueOf(claims.get("sub").toString());
	}

	private String secretByTokenKid(String token) {
		DecodedJWT unverifiedJwt = JWT.decode(token);
		return secretKeyId.equalsIgnoreCase(unverifiedJwt.getKeyId()) ? secretKey : legacySecretKey;
	}

	public Set<GrantedAuthority> getGrantedAuthority(String token, UserPrincipal userPrincipal) throws IOException {
		Claims claims = Jwts.parser()
				.setSigningKey(secretByTokenKid(token))
				.parseClaimsJws(token)
				.getBody();
		Set<GrantedAuthority> result = Sets.newHashSet();
		if (StringUtils.hasText(claims.get("roles").toString())) {
			String[] listRole = claims.get("roles").toString().split(",");
			for (String role : listRole) {
				SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
				result.add(grantedAuthority);
			}
		}
		if (claims.get("resourceAccess") != null) {
			Map<String, Map<String, List<String>>> mapResourceAccess = (Map<String, Map<String, List<String>>>) claims.get("resourceAccess");
			userPrincipal.setResourceAccess(mapResourceAccess);
		}

		return result;
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(secretByTokenKid(authToken)).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			ex.printStackTrace();
			log.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			ex.printStackTrace();
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			ex.printStackTrace();
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			ex.printStackTrace();
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			log.error("JWT claims string is empty.");
		}
		return false;
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
	
	public String getEmailFromToken(String token, String jwkProviderUrl) throws JwkException {
		DecodedJWT jwt = JWT.decode(token);
		JwkProvider provider = new UrlJwkProvider(jwkProviderUrl);
		Jwk jwk = provider.get(jwt.getKeyId());
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
		algorithm.verify(jwt);

		// Check expiration
		if (jwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
		  throw new RuntimeException("Exired token!");
		}
		return jwt.getClaim("email").asString();
	}

	public ExchangeTokenUserDto getUserIdFromExchangeToken(String exchangeToken) {
		Claims claims = Jwts.parser()
				.setSigningKey(secretByTokenKid(exchangeToken))
				.parseClaimsJws(exchangeToken)
				.getBody();
		ExchangeTokenUserDto exchangeTokenUserDto = new ExchangeTokenUserDto();
		exchangeTokenUserDto.setId(Long.valueOf(claims.get("uid").toString()));
		exchangeTokenUserDto.setUserId(Long.valueOf(claims.get("sub").toString()));
		return exchangeTokenUserDto;
	}

}
