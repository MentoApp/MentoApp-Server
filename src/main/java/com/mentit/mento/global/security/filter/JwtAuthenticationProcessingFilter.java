package com.mentit.mento.global.security.filter;

import com.mentit.mento.domain.users.repository.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.JwtException;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.jwt.service.JwtService;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
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
    private final UserRepository userRepository;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/swagger", "/v3/api-docs", "/login", "/favicon"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("request.URI = {}", request.getRequestURI());

        String requestURI = request.getRequestURI();

        if (isExcludedPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token == null) {
            handleJwtException(response, new JwtException(ExceptionCode.NOT_FOUND_TOKEN));
            return;
        }

        try {
            if (jwtService.validateToken(token)) {
                Authentication authentication = jwtService.getAuthenticationFromAccessToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.warn("JWeT Exception", e);
            throw new JwtException(ExceptionCode.INVALID_TOKEN);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetail userDetail) {
            userRepository.findById(userDetail.getId()).orElseThrow(
                    () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
            );
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcludedPath(String requestURI) {
        return EXCLUDE_URLS.stream().anyMatch(requestURI::startsWith);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.info("token={}", token);
            return token;
        }
        return null;
    }

    private void handleJwtException(HttpServletResponse response, JwtException e) throws IOException {
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
