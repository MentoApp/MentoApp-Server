package com.mentit.mento.domain.users.controller;

import com.mentit.mento.domain.users.dto.request.SignInUserRequest;
import com.mentit.mento.domain.users.dto.request.ModifyUserRequest;
import com.mentit.mento.domain.users.service.UserService;
import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;
    private final CookieUtils cookieUtils;

    @Operation(summary = "회원 정보 추가 기입", description = "회원 정보 추가 기입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> createUser(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Valid @RequestPart(value = "signInRequest") SignInUserRequest signInUserRequest,
        @RequestPart(value = "profileImage",required = false) MultipartFile profileImage
            ) {

        userService.create(userDetail, signInUserRequest,profileImage);

        return Response.success(HttpStatus.OK,"회원가입 성공");
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보 기입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 수정 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "정보 수정 실패")
    })
    @PatchMapping( value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> modifyUser(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @Valid @RequestPart("modifyUserRequest") ModifyUserRequest modifyUserRequest,
            @RequestPart(value = "profileImage",required = false) @Nullable MultipartFile profileImage
    ) {
        userService.modifyUser(customUserDetail,modifyUserRequest,profileImage);

        return Response.success(HttpStatus.OK,"회원정보 수정 성공");

    }

//    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "정보 조회 성공",
//                    content = {@Content(schema = @Schema(implementation = Response.class))}),
//            @ApiResponse(responseCode = "400", description = "정보 조회 실패")
//    })
//    @GetMapping
//    public Response<FindUserResponse>

    @Operation(summary = "토큰 재발급", description = "accessToken을 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "발급 실패")
    })
    @GetMapping("/reissue-token")
    @Transactional
    public ResponseEntity<String> reissue(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletResponse response,
            HttpServletRequest request
    ) {

        String refreshToken = cookieUtils.getRefreshToken(request);
        JwtToken newToken = userService.reissueToken(refreshToken);
        cookieUtils.addCookie(response, "refreshToken", newToken.getRefreshToken(), 24 * 60 * 60 * 7);

        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", newToken.getAccessToken());
        headers.add("refreshToken", newToken.getRefreshToken());

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @Operation(summary = "소셜 회원 탈퇴", description = "소셜 회원은 재로그인을 통해 검증, 재발급 받은 액세스 토큰을 통해 서비스 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 회원 탈퇴 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "해당 소셜 회원이 존재하지 않습니다.")
    })
    @DeleteMapping("/social/me")
    public ResponseEntity<Void> deleteSocialMember(
            @AuthenticationPrincipal CustomUserDetail user
    ) {
        userService.deleteSocialMember(user.getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
