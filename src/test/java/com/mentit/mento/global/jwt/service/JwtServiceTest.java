package com.mentit.mento.global.jwt.service;


import com.mentit.mento.domain.auth.repository.UserRepository;
import com.mentit.mento.global.authToken.repository.RefreshTokenRepository;
import com.mentit.mento.global.exception.customException.JwtException;
import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    private Key key;
    private final long accessTokenExpirationTime = 3600000;
    private final long refreshTokenExpirationTime = 1209600000;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        jwtService = new JwtService("aowieurlwkj12309dfusoirm20394u123sdfasdlfkui2d02kd783", accessTokenExpirationTime, refreshTokenExpirationTime, refreshTokenRepository, userRepository);
        ReflectionTestUtils.setField(jwtService, "key" , key);
    }

    @Test
    void testGenerateToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetail("user@example.com", "", 1L, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))),
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        JwtToken jwtToken = jwtService.generateToken(authentication);
        assertNotNull(jwtToken.getAccessToken());
        assertNotNull(jwtToken.getRefreshToken());
    }

    @Test
    void testValidateToken() {
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .claim("auth", "ROLE_USER")
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(key)
                .compact();

        assertTrue(jwtService.validateToken(token));

    }

    @Test
    void testValidateToken_ExpiredToken() {
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .claim("auth", "ROLE_USER")
                .setExpiration(new Date(System.currentTimeMillis() - accessTokenExpirationTime))
                .signWith(key)
                .compact();

        JwtException exception = assertThrows(JwtException.class, () -> jwtService.validateToken(token));
        assertEquals("Token has expired", exception.getMessage());
    }

    @Test
    void testGetAuthenticationFromAccessToken() {
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .claim("auth", "ROLE_USER")
                .claim("id", 1L)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(key)
                .compact();

        Authentication authentication = jwtService.getAuthenticationFromAccessToken(token);
        assertNotNull(authentication);
        assertEquals("user@example.com", authentication.getName());
    }
    }