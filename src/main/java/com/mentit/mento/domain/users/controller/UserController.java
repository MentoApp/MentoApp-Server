package com.mentit.mento.domain.users.controller;

import com.mentit.mento.domain.users.dto.request.FindUserDTO;
import com.mentit.mento.domain.users.dto.request.SeekerSignUpRequestDTO;
import com.mentit.mento.domain.users.dto.request.WorkerSignUpRequestDTO;
import com.mentit.mento.domain.users.entity.Role;
import com.mentit.mento.domain.users.service.UserService;
import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;
    private final CookieUtils cookieUtils;


    @Operation(summary = "현직자 회원가입", description = "소셜 가입 후 현직자 정보 기입api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "400", description = "발급 실패")
    })
    @PostMapping("/sign-up-worker")
    public Response<Void> SignUpByWorker(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody WorkerSignUpRequestDTO workerSignUpRequestDTO
            ) {

        userService.signUpByWorker(userDetail, workerSignUpRequestDTO);

        return Response.success(HttpStatus.OK, "현직자 회원가입 완료");
    }

    @Operation(summary = "구직자 회원가입", description = "소셜 가입 후 구직자 정보 기입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "400", description = "발급 실패")
    })
    @PostMapping("/sign-up-seeker")
    public Response<Void> SignUpBySeeker(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody String preferredJob
    ) {

        userService.signUpBySeeker(userDetail, preferredJob);

        return Response.success(HttpStatus.OK, "구직자 회원가입 완료");
    }

    @Operation(summary = "내 정보 조회", description = "구직자/현직자별로 정보 조회가 다름")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "400", description = "발급 실패")
    })
    @Parameter(name = "roleType", description = "MENTEE or MENTOR", example = "MENTEE", required = true, schema = @Schema(type = "string"))
    @GetMapping("/me/{roleType}")
    public Response<FindUserDTO> retrieveMyInfo(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable String roleType
    ) {
        FindUserDTO findUserInfo = userService.findMyInfo(userDetail, roleType);

        return Response.success(HttpStatus.OK, "회원정보 조회 완료",findUserInfo);

    }

    @Operation(summary = "토큰 재발급", description = "accessToken을 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발급 성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "400", description = "발급 실패")
    })
    @GetMapping("/reissue-token")
    @Transactional
    public ResponseEntity<String> reissue(
            @AuthenticationPrincipal CustomUserDetail userDetail, HttpServletResponse response, HttpServletRequest request) {

        String refreshToken = cookieUtils.getRefreshToken(request);
        JwtToken newToken = userService.reissueToken(refreshToken);
//        App에는 Cookie개념이 없기 때문에 사용하지 않음
        cookieUtils.addCookie(response, "refreshToken", newToken.getRefreshToken(), 24 * 60 * 60 * 7);

        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", newToken.getAccessToken());
        headers.add("refreshToken", newToken.getRefreshToken());

        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
    }

    @Operation(summary = "소셜 회원 탈퇴", description = "소셜 회원은 재로그인을 통해 검증, 재발급 받은 액세스 토큰을 통해 서비스 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 회원 탈퇴 성공",
                    content = {@Content(schema = @Schema(implementation = ResponseEntity.class))}),
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
