package vuly.thesis.ecowash.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.exception.AppExceptionResponse;
import vuly.thesis.ecowash.core.util.ExceptionBuilderUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request,
						 HttpServletResponse response,
						 AuthenticationException e) throws IOException {

		log.error("Responding with unauthorized error. Message - {}", e.getMessage());

		String uri = request.getScheme() + "://" +   // "http" + "://
				request.getServerName() +       // "myhost"
				":" +                           // ":"
				request.getServerPort() +       // "8080"
				request.getRequestURI() +       // "/people"
				"?" +                           // "?"
				request.getQueryString();       // "lastname=Fox&age=30"
		log.error("uri: " + uri);
		String token = request.getHeader("Authorization");
		String atoken = request.getHeader("Access-Control-Allow-Origin");
		String cont = request.getHeader("Content-Type");
		log.error("cont: " + cont);
		log.error("atoken: " + atoken);
		log.error("token: " + token);


		String language = request.getHeader("Accept-Language");
		if (language == null) {
			language = "en";
		}
		LocaleContextHolder.setLocale(Locale.forLanguageTag(language));
		AppExceptionResponse ex = ExceptionBuilderUtil.exceptionResponseBuilder(
				new AppException(HttpStatus.UNAUTHORIZED, 4001, null));

		//All authentication exception are return 401
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		response.getWriter().write(objectMapper.writeValueAsString(ex));
	}
}
