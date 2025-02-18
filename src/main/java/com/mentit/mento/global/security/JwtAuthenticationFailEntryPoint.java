package com.mentit.mento.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentit.mento.global.exception.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAuthenticationFailEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = request.getAttribute("message").toString();
        int code = (int) request.getAttribute("code");


        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(ExceptionResponse.builder().httpStatus(HttpStatus.FORBIDDEN).status("ERROR").message(message).code(code).build());

        response.getWriter().write(result);
    }
}
