package com.mentit.mento.global.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CookieUtils {

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        // ResponseCookie를 생성
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서는 true로 설정
                .maxAge(maxAge) // 만료 시간 설정
                .domain("15.165.4.143") // 도메인 설정
                .sameSite("None") // SameSite 설정 (CORS 지원을 위해 None)
                .build();

        // 응답 헤더에 쿠키 추가
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String keyName) {
        // ResponseCookie를 생성 (만료 시간 0으로 설정)
        ResponseCookie cookie = ResponseCookie.from(keyName, "")
                .path("/")
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서는 true로 설정
                .maxAge(0) // 쿠키 만료
                .domain("15.165.4.143") // 도메인 설정
                .sameSite("None") // SameSite 설정 (CORS 지원을 위해 None)
                .build();

        // 응답 헤더에 쿠키 추가
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        Arrays.stream(cookies).toList().forEach(i -> System.out.println(i.getName()));

        Optional<String> refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue);
        return refreshToken.orElse(null);
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
