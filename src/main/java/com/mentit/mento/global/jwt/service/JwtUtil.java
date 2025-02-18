package com.mentit.mento.global.jwt.service;

import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.JwtException;
import com.mentit.mento.global.helper.UserHelper;
import com.mentit.mento.global.redis.service.RedisService;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpirationTime;
    private final RedisService redisService;

    private static final String PREFIX_ISSUED_AT = "token_issued_at";
    private final UserHelper userHelper;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.token.access-token-expiration-time}") long accessTokenExpirationTime,
                   RedisService redisService,
                   UserHelper userHelper) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.redisService = redisService;
        this.userHelper = userHelper;
    }

    public String generateToken(UsersEntity usersEntity) {
        String accessToken = generateAccessToken(usersEntity);

        redisService.updateTokenStatus(usersEntity);

        return accessToken;
    }

    @Transactional
    public String reissueToken(String accessToken) {
        LocalDateTime issuedTimeFromToken = getIssuedTimeFromToken(accessToken);
        Long userId = getUserIdFromToken(accessToken);
        UsersEntity usersEntity = userHelper.getUsers(userId);
        log.info("extracted Token IssuedTime = {} " , issuedTimeFromToken.toString());
        log.info("userEntity IssuedTime = {} ", usersEntity.getTokenIssuedAt().toString());
        if (issuedTimeFromToken.isEqual(usersEntity.getTokenIssuedAt())) {
            // 새로운 토큰 생성
            return generateToken(usersEntity);
        } else {
            throw new JwtException(ExceptionCode.INVALID_TOKEN);
        }

    }

    public Authentication getAuthenticationFromAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new JwtException(ExceptionCode.INVALID_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        CustomUserDetail userDetail = new CustomUserDetail(claims.getSubject(), "",
                Long.parseLong(String.valueOf(claims.get("id"))), authorities);
        return new UsernamePasswordAuthenticationToken(userDetail, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw handlingJwtException(e);
        }
    }

    private String generateAccessToken(UsersEntity usersEntity) {

        String authorities = usersEntity.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(usersEntity.getName())
                .claim("id", usersEntity.getUserId())
                .claim("auth", authorities)
                .claim(PREFIX_ISSUED_AT, LocalDateTime.now().toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private JwtException handlingJwtException(Exception e) {
        if (e instanceof SecurityException || e instanceof MalformedJwtException) {
            return new JwtException(ExceptionCode.INVALID_TOKEN);
        } else if (e instanceof ExpiredJwtException) {
            return new JwtException(ExceptionCode.TOKEN_EXPIRED);
        } else if (e instanceof UnsupportedJwtException) {
            return new JwtException(ExceptionCode.UNSUPPORTED_TOKEN);
        } else if (e instanceof IllegalArgumentException) {
            return new JwtException(ExceptionCode.NOT_FOUND_TOKEN);
        } else {
            return new JwtException();
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token); // parseClaims는 이미 클래스에 구현되어 있음
        Number id = (Number) claims.get("id"); // "id" 클레임을 Number로 캐스팅
        return id.longValue(); // Number 타입에서 Long으로 변환
    }

    public String subString(String token) {
        if (token.contains("Bearer")) {
            return token.substring(7);
        }
        return token;
    }

    public LocalDateTime getIssuedTimeFromToken(String token) {
        Claims claims = parseClaims(token);
        return LocalDateTime.parse((CharSequence) claims.get(PREFIX_ISSUED_AT));
    }

    public String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        log.info("Authorization header is missing or does not contain a Bearer token");
        return null;
    }


}