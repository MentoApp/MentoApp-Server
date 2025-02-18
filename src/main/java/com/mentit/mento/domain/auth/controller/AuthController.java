package com.mentit.mento.domain.auth.controller;

import com.mentit.mento.domain.auth.dto.SocialAccountInfoDto;
import com.mentit.mento.domain.auth.dto.SocialAccountInfoResponse;
import com.mentit.mento.domain.auth.service.AuthService;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.UserService;
import com.mentit.mento.global.helper.UserHelper;
import com.mentit.mento.global.jwt.service.JwtUtil;
import com.mentit.mento.global.redis.service.RedisService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CookieUtils cookieUtils;
    private final RedisService redisService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserHelper userHelper;

    @Operation(
            summary = "회원 정보 등록 또는 업데이트",
            description = "소셜 로그인 후 프론트단에서 제공하는 유저의 정보로 유저 가입 또는 기존 정보를 업데이트 합니다. " +
                    "전화번호 = 000-0000-000 , 성별은 MALE/FEMALE , 생년은 YYYY , 생일은 MMDD 입니다. authType은 kakao 또는 naver로 기재해주시면 됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 발급 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = SocialAccountInfoResponse.class
                            )
                    ),
                    headers = {
                            @Header(name = "Authorization-Access", description = "발급된 Access 토큰", schema = @Schema(type = "string")),
                            @Header(name = "Authorization-Refresh", description = "발급된 Refresh 토큰", schema = @Schema(type = "string"))
                    }
            ),
            @ApiResponse(
                    responseCode = "811",
                    description = "이미 카카오 계정으로 등록된 사용자입니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\":\"CONFLICT\",\"message\":\"카카오로 가입된 계정이 존재합니다.\",\"code\":811}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "812",
                    description = "이미 네이버 계정으로 등록된 사용자입니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"status\":\"CONFLICT\",\"message\":\"네이버로 가입된 계정이 존재합니다.\",\"code\":812}"
                            )
                    )
            )
    })
    @PostMapping("/social/account-info")
    public Response<Map<String, Boolean>> getSocialAccountInfo(
            @RequestBody SocialAccountInfoDto socialAccountInfoDto,
            HttpServletResponse resp
    ) {
        //유저 확인 또는 새로 생성
        UsersEntity user = authService.getOrCreateUserInfo(socialAccountInfoDto);
        //token 생성
        String accessToken = jwtUtil.generateToken(user);
        //token 발급시간 업데이트
        userService.updateUserTokenStatus(user,accessToken);
        //토큰 캐싱
        redisService.updateTokenStatus(user);

        resp.setHeader("Authorization", "Bearer " + accessToken);

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("isNewUser", user.isNewUser());

        return Response.success(HttpStatus.OK, "토큰 발급 완료", map);
    }

    @Operation(summary = "토큰 재발급", description = "기존 요청과 동일하게 accessToken을 담아주시면 되겠습니다!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))})
    })
    @GetMapping("/reissue-token")
    @Transactional
    public Response<Void> reissue(
            @RequestHeader("Authorization") String accessToken,
            HttpServletResponse resp
    ) {
        accessToken = jwtUtil.subString(accessToken);
        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        String newAccessToken = jwtUtil.reissueToken(accessToken,userId);
        UsersEntity usersEntity = userHelper.getUsers(userId);
        userService.updateUserTokenStatus(usersEntity,newAccessToken);

        redisService.updateTokenStatus(usersEntity);

        resp.setHeader("Authorization", newAccessToken);

        return Response.success(HttpStatus.OK,"토큰 재발급 완료");
    }

    @Operation(summary = "소셜 회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 회원 탈퇴 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "해당 소셜 회원이 존재하지 않습니다.",
                    content = {@Content(schema = @Schema(implementation = Exception.class))}),
    })
    @DeleteMapping("/social/me")
    public Response<Void> deleteSocialMember(
            @RequestHeader("Authorization") String BearerToken
    ) {
        authService.deleteSocialMember(BearerToken);

        return Response.success(HttpStatus.OK, "탈퇴 성공");

    }

    @Operation(summary = "로그아웃", description = "DB에 저장된 리프레쉬 토큰을 사용하여 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰이 쿠키에 없습니다.")
    })
    @PostMapping("/logout")
    public Response<Void> logout(
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        // 로그아웃 처리
        authService.logout(userDetail.getId());

        return Response.success(HttpStatus.OK, "로그아웃 성공");
    }
}
