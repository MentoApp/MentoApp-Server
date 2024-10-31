package com.mentit.mento.global.oauth.handler;

import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.domain.users.repository.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
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
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();

        Users findUser = userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );

        String accessToken = jwtToken.getAccessToken();

        String refreshToken = jwtToken.getRefreshToken();

        redisService.saveAccessToken(accessToken,userDetail.getId());

        cookieUtils.addCookie(response, "refreshToken", refreshToken, 24 * 60 * 60 * 7); // 7일 동안 유효한 쿠키


        response.addHeader("isNewUser", String.valueOf(findUser.isNewUser()));
        response.addHeader("accessToken", accessToken);
    }
}
