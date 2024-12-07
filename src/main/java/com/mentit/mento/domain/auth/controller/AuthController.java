package com.mentit.mento.domain.auth.controller;

import com.mentit.mento.domain.auth.service.AuthService;
import com.mentit.mento.domain.users.service.UserCreateService;
import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.redis.service.RedisService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final UserCreateService userCreateService;
    private final CookieUtils cookieUtils;
    private final RedisService redisService;
    private final AuthService authService;

    @Operation(summary = "토큰 재발급", description = "accessToken을 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))})
            })
    @GetMapping("/reissue-token")
    @Transactional
    public ResponseEntity<String> reissue(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletResponse response,
            HttpServletRequest request
    ) {

//        String refreshToken = cookieUtils.getRefreshToken(request);
        String refreshToken = request.getHeader("refreshToken");
        JwtToken newToken = authService.reissueToken(refreshToken);
//        cookieUtils.addCookie(response, "refreshToken", newToken.getRefreshToken(), 24 * 60 * 60 * 7);
        redisService.saveAccessToken(newToken.getAccessToken(), userDetail.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", newToken.getAccessToken());
        headers.add("refreshToken", newToken.getRefreshToken());

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @Operation(summary = "소셜 회원 탈퇴", description = "소셜 회원은 재로그인을 통해 검증, 재발급 받은 액세스 토큰을 통해 서비스 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 회원 탈퇴 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "해당 소셜 회원이 존재하지 않습니다.",
                    content = {@Content(schema = @Schema(implementation = Exception.class))}),
    })
    @DeleteMapping("/social/me")
    public Response<Void> deleteSocialMember(
            @AuthenticationPrincipal CustomUserDetail user,
            HttpServletResponse response

    ) {
        authService.deleteSocialMember(user.getId());
        cookieUtils.deleteCookie(response, "refreshToken");

        return Response.success(HttpStatus.OK, "탈퇴 성공");

    }

    @Operation(summary = "로그아웃", description = "DB에 저장된 리프레쉬 토큰을 사용하여 로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰이 쿠키에 없습니다.")
    })
    @PostMapping("/logout")
    public Response<Void> logout(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletResponse response
    ) {

        // 로그아웃 처리
        authService.logout(userDetail.getId());

        // 쿠키에서 refreshToken 삭제
        cookieUtils.deleteCookie(response, "refreshToken");

        return Response.success(HttpStatus.OK, "로그아웃 성공");
    }
}
