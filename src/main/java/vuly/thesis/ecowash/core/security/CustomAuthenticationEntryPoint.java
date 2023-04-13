package vuly.thesis.ecowash.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.exception.AppExceptionResponse;
import vuly.thesis.ecowash.core.util.ExceptionBuilderUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e)
            throws IOException, ServletException {

        log.error("Unauthorized error. Message - {}", e.getMessage());
        AppExceptionResponse ex = ExceptionBuilderUtil.exceptionResponseBuilder(
                new AppException(HttpStatus.UNAUTHORIZED, 4001, null));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.getWriter().write(objectMapper.writeValueAsString(ex));
    }
}
