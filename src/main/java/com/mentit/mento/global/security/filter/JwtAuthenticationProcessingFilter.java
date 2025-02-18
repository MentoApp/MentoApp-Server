package com.mentit.mento.global.security.filter;

import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.JwtException;
import com.mentit.mento.global.helper.UserHelper;
import com.mentit.mento.global.jwt.service.JwtUtil;
import com.mentit.mento.global.redis.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final UserHelper userHelper;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/css", "/swagger", "/v3/api-docs", "/login", "/favicon",
            "/api/v1/auth/reissue-token",
            "/api/v1/auth/social/account-info",
            "/api/v1/auth/social/me",
            "/api/v1/user/tags",
            "api/v1/auth/social/account-info"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("Processing request: request.URI = {}", request.getRequestURI());
        //인증이 필요없는 경로는 다음 필터 수행
        String requestURI = request.getRequestURI();

        if (isExcludedPath(requestURI)) {
            log.info("Request URI is excluded from filtering: {}", requestURI);
            filterChain.doFilter(request, response);
        } else {
            log.info("수행된거냐");
            //헤더의 토큰에서 accessToken 추출
            String token = jwtUtil.resolveToken(request.getHeader("Authorization"));

            try {
                if (token != null && jwtUtil.validateToken(token)) {
                    LocalDateTime issuedTimeFromToken = jwtUtil.getIssuedTimeFromToken(token);
                    log.info("Access token is valid");
                    Long userId = jwtUtil.getUserIdFromToken(token);

                    String issuedTimeFromRedis = redisService.getAccessToken(userId);

                    // Redis에 저장된 토큰과 요청의 토큰이 다르면 중복 로그인으로 간주
                    if (issuedTimeFromRedis != null &&
                            !issuedTimeFromToken.isEqual(LocalDateTime.parse(issuedTimeFromRedis))) {
                        throw new JwtException(ExceptionCode.DUPLICATE_LOGIN);
                    } else if (issuedTimeFromRedis == null) {
                        UsersEntity usersEntity = userHelper.getUsers(userId);
                        if (usersEntity.getTokenIssuedAt().isEqual(issuedTimeFromToken)) {
                            throw new JwtException(ExceptionCode.DUPLICATE_LOGIN);
                        }
                    }
                    Authentication authentication = jwtUtil.getAuthenticationFromAccessToken(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication object created successfully for user: {}", authentication.getName());
                }
            } catch (JwtException e) {
                log.error(e.getMessage());
                request.setAttribute("httpStatus", e.getHttpStatus());
                request.setAttribute("message", e.getMessage());
                request.setAttribute("code", e.getExceptionCode());
            }
        }
    }

    private boolean isExcludedPath(String requestURI) {
        log.info("{} 경로 매칭중", requestURI);
        boolean isExcluded = EXCLUDE_URLS.stream().anyMatch(requestURI::equalsIgnoreCase);
        log.info("Checking if request URI is excluded: {} -> {}", requestURI, isExcluded);
        return isExcluded;
    }



}