package com.mentit.mento.global.security.entryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ExceptionResponse exceptionResponse = ExceptionResponse.fromException(ExceptionCode.INVALID_TOKEN); // 토큰 관련 예외로 처리

        response.setStatus(exceptionResponse.httpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // JSON으로 변환하여 응답
        String jsonResponse = objectMapper.writeValueAsString(exceptionResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}