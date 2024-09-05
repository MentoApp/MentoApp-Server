package com.mentit.mento.global.oauth.handler;

import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.jwt.service.JwtService;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);


        // access token 헤더에 담기
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/login")
                .queryParam("accessToken", jwtToken.getAccessToken())
                .build().toUriString();

        String refreshToken = jwtToken.getRefreshToken();

        cookieUtils.addCookie(response, "refreshToken", refreshToken, 24 * 60 * 60 * 7); // 7일

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
