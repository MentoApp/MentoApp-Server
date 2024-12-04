package com.mentit.mento.global.security.filter;

import com.mentit.mento.domain.users.infrastructure.UserRepositoryImpl;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.JwtException;
import com.mentit.mento.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepositoryImpl userRepositoryImpl;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/css", "/swagger", "/v3/api-docs", "/login", "/favicon","api/v1/auth"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Processing request: request.URI = {}", request.getRequestURI());

        String requestURI = request.getRequestURI();

        if (isExcludedPath(requestURI)) {
            log.info("Request URI is excluded from filtering: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token == null) {
            log.warn("Access token not found in the request");
            handleJwtException(response, new JwtException(ExceptionCode.NOT_FOUND_TOKEN)); // 예외를 handleJwtException 메서드로 처리
            return;
        }

        log.info("Access token resolved successfully");

        // refreshToken 가져오기
        String refreshToken = resolveRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            log.warn("Refresh token not found in the request cookies");
            handleJwtException(response, new JwtException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN));
            return;
        }

        log.info("Refresh token resolved successfully");

        try {
            if (jwtService.validateToken(token)) {
                log.info("Access token is valid");
                Authentication authentication = jwtService.getAuthenticationFromAccessToken(token);
                log.info("Authentication object created successfully for user: {}", authentication.getName());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage(), e);
            handleJwtException(response, new JwtException(ExceptionCode.INVALID_TOKEN)); // 예외를 handleJwtException 메서드로 처리
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcludedPath(String requestURI) {
        boolean isExcluded = EXCLUDE_URLS.stream().anyMatch(requestURI::startsWith);
        log.debug("Checking if request URI is excluded: {} -> {}", requestURI, isExcluded);
        return isExcluded;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("Extracted token from Authorization header: {}", token);
            return token;
        }
        log.debug("Authorization header is missing or does not contain a Bearer token");
        return null;
    }

    private void handleJwtException(HttpServletResponse response, JwtException e) throws IOException {
        log.error("Handling JWT exception: {} - {}", e.getExceptionCode().getCode(), e.getExceptionCode().getMessage());
        response.setStatus(e.getExceptionCode().getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"status\":  \"%s\", \"code\": %d, \"message\": \"%s\"}",
                e.getExceptionCode().getHttpStatus().name(),
                e.getExceptionCode().getCode(),
                e.getExceptionCode().getMessage()
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    // 쿠키에서 refreshToken 가져오기
    private String resolveRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                log.debug("Inspecting cookie: name = {}, value = {}", cookie.getName(), cookie.getValue());
                if ("refreshToken".equals(cookie.getName())) {
                    log.debug("Refresh token found in cookies");
                    return cookie.getValue();
                }
            }
        }
        log.debug("No refresh token found in cookies");
        return null;
    }
}