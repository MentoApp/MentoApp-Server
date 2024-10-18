package com.mentit.mento.global.oauth.handler;

import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.jwt.service.JwtService;
import com.mentit.mento.global.redis.service.RedisService;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtTokenProvider;
    private final CookieUtils cookieUtils;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();

        String targetUrl;

        if (userDetail.isNewUser()) {
            // 새로운 사용자라면 /add-information 페이지로 리디렉션
            targetUrl = "http://localhost:8080/api/v1/test/for-redirect?isNewUser=true";
        } else {
            // 기존 사용자라면 메인 페이지로 리디렉션
            targetUrl = "http://localhost:8080/api/v1/test/for-redirect?isNewUser=false";
        }

        String accessToken = jwtToken.getAccessToken();

        String refreshToken = jwtToken.getRefreshToken();

        redisService.saveAccessToken(accessToken,userDetail.getId());

        cookieUtils.addCookie(response, "refreshToken", refreshToken, 24 * 60 * 60 * 7); // 7일 동안 유효한 쿠키

        // 토큰을 URL 파라미터로 추가
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
