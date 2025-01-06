package com.mentit.mento.global.security.filter;

import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.JwtException;
import com.mentit.mento.global.jwt.service.JwtService;
import com.mentit.mento.global.redis.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
    private final RedisService redisService;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/css", "/swagger", "/v3/api-docs", "/login", "/favicon", "/api/v1/auth/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Processing request: request.URI = {}", request.getRequestURI());

        //인증이 필요없는 경로는 다음 필터 수행
        String requestURI = request.getRequestURI();

        if (isExcludedPath(requestURI)) {
            log.info("Request URI is excluded from filtering: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //헤더의 토큰에서 accessToken 추출
        String token = resolveToken(request);
        if (token == null) {
            log.warn("Access token not found in the request");
            handleJwtException(response, new JwtException(ExceptionCode.NOT_FOUND_TOKEN)); // 예외를 handleJwtException 메서드로 처리
            return;
        }
        log.info("Access token resolved successfully");

        //검증 성공시 SecurityContextHolder에 인증된 유저의 authentication을 저장한다.
        try {
            if (jwtService.validateToken(token)) {
                log.info("Access token is valid");
                Long userId = jwtService.getUserIdFromToken(token);
                String redisToken = redisService.getAccessToken(String.valueOf(userId));
                log.info("현재 저장된 토큰={}", redisToken);

                // Redis에 저장된 토큰과 요청의 토큰이 다르면 중복 로그인으로 간주
                if (redisToken != null && !redisToken.equals(token)) {
                    handleJwtException(response, new JwtException(ExceptionCode.DUPLICATE_LOGIN));
                }
                Authentication authentication = jwtService.getAuthenticationFromAccessToken(token);
                log.info("Authentication object created successfully for user: {}", authentication.getName());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage(), e);
            //예외를 GlobalException에서 처리할 수 있도록 변경해야 한다.
            handleJwtException(response, new JwtException(ExceptionCode.INVALID_TOKEN)); // 예외를 handleJwtException 메서드로 처리
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcludedPath(String requestURI) {
        log.info("{} 경로 매칭중", requestURI);
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

}