package com.mentit.mento.domain.users.controller;

import com.mentit.mento.domain.users.domain.dto.request.ModifyUser;
import com.mentit.mento.domain.users.domain.dto.request.SignInUser;
import com.mentit.mento.domain.users.domain.dto.response.FindUserAccountResponse;
import com.mentit.mento.domain.users.domain.dto.response.FindUserResponse;
import com.mentit.mento.domain.users.dto.request.TagListDTO;
import com.mentit.mento.domain.users.service.UserCreateService;
import com.mentit.mento.domain.users.service.UserService;
import com.mentit.mento.global.redis.service.RedisService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserCreateService userCreateService;
    private final CookieUtils cookieUtils;
    private final RedisService redisService;
    private final UserService userService;

    @Operation(summary = "계정 추가 정보 가입", description = "게정 추가 정보를 가입하고 isNewUser를 true로 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 성공!"),
            @ApiResponse(responseCode = "401", description = "인증 문제 발생"),
            @ApiResponse(responseCode = "803", description = "회원을 찾을 수 없음"),

    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> createUser(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestPart(value = "signInUserRequest") SignInUser signInUser,
            @RequestPart(value = "profileImage") MultipartFile profileImage
    ) {

        userCreateService.create(userDetail, signInUser, profileImage);

        return Response.success(HttpStatus.OK, "회원가입 성공");
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보 기입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 문제 발생"),
            @ApiResponse(responseCode = "803", description = "회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "810", description = "유저 태그를 찾을 수 없음"),
    })
    @PatchMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> modifyUser(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @Valid @RequestPart("modifyUserRequest") ModifyUser modifyUserRequest,
            @RequestPart(value = "profileImage") @Nullable MultipartFile profileImage
    ) {
        userCreateService.modifyUser(customUserDetail, modifyUserRequest, profileImage);

        return Response.success(HttpStatus.OK, "회원정보 수정 성공");
    }

    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "정보 조회 실패",
                    content = {@Content(schema = @Schema(implementation = Exception.class))}),
    })
    @GetMapping("/myInfo")
    public Response<FindUserResponse> findMyInfo(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        FindUserResponse findUserResponse = userService.findMyInfo(userDetail);
        return Response.success(HttpStatus.OK, "회원 조회 성공", findUserResponse);
    }

    @Operation(summary = "계정 정보 조회", description = "계정 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정정보 조회 결과",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "계정정보 조회 실패",
                    content = {@Content(schema = @Schema(implementation = Exception.class))}),
    })
    @GetMapping("/myAccountInfo")
    public Response<FindUserAccountResponse> findMyAccountInfo(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        FindUserAccountResponse findUserAccountResponse = userCreateService.findMyAccountInfo(userDetail);
        return Response.success(HttpStatus.OK, "계정 정보 조회 성공", findUserAccountResponse);
    }

    @Operation(summary = "닉네임 중복 검사", description = "닉네임 중복 조회(true : 가능 / false : 불가능), 내 닉네임을 내가 조회할 경우에도 true 반환")
    @GetMapping("/validate-nickname/{nickname}")
    public Response<String> validateNickname(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable String nickname
    ) {
        boolean flag = userService.validateNickname(nickname, userDetail);

        return Response.success(HttpStatus.OK, "조회 결과", flag + "");
    }

    @Operation(summary = "내 추천 게시물 키워드 편집", description = "기존의 게시물 키워드를 삭제하고 업데이트합니다.")
    @PutMapping("/modify-boardKeyword")
    public Response<Void> modifyBoardKeyword(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @ModelAttribute List<String> modifyBoardKeyword
    ) {
        userService.modifyBoardKeyword(userDetail, modifyBoardKeyword);

        return Response.success(HttpStatus.OK, "키워드 갱신 완료");
    }

    @GetMapping("/tags")
    public Response<TagListDTO> getTagLists(
    ) {
        TagListDTO tagListDTO = userService.getTagsLists();

        return Response.success(HttpStatus.OK, "키워드 조회 완료", tagListDTO);
    }

}
