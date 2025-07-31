package tz.business.eCard.security;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("REQUEST :  " + httpServletRequest.getPathInfo()  +" METHOD  : " + httpServletRequest.getMethod());
        log.error("Unauthorized error: {}", authException.getMessage());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        AuthError error = new AuthError();
        error.setErrors(authException.getMessage());
        error.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        String json = new Gson().toJson(error);
        response.getOutputStream().println(json);
    }

    @Setter
    @Getter
    static class AuthError{
        private String errors;
        private int code;
    }
}
