package com.mentit.mento.global.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CookieUtils {

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        // Secure 속성 설정하지 않음
        // SameSite 속성 설정을 위해 응답 헤더에 추가
        response.addHeader("Set-Cookie", String.format("%s=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=None", name, value, maxAge));
        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletResponse response, String keyName) {
        Cookie cookie = new Cookie(keyName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // Secure 속성 설정하지 않음
        response.addHeader("Set-Cookie", String.format("%s=; Path=/; HttpOnly; Max-Age=0; SameSite=None", keyName));
        response.addCookie(cookie);
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
