package com.mentit.mento.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ExceptionResponse exceptionResponse = ExceptionResponse.fromException(ExceptionCode.ACCESS_DENIED); // 권한 관련 예외 처리

        response.setStatus(exceptionResponse.httpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // JSON으로 변환하여 응답
        String jsonResponse = objectMapper.writeValueAsString(exceptionResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}