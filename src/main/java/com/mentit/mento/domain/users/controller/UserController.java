package com.mentit.mento.domain.users.controller;

import com.mentit.mento.domain.users.domain.dto.request.ModifyUserRequest;
import com.mentit.mento.domain.users.domain.dto.request.SignInUserRequest;
import com.mentit.mento.domain.users.domain.dto.response.FindUserAccountResponse;
import com.mentit.mento.domain.users.domain.dto.response.FindUserResponse;
import com.mentit.mento.domain.users.dto.request.TagListDTO;
import com.mentit.mento.domain.users.service.UserCreateService;
import com.mentit.mento.domain.users.service.UserService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
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
    private final UserService userService;

    @Operation(summary = "계정 추가 정보 가입", description = "게정 추가 정보를 가입하고 isNewUser를 true로 반환합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> createUser(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestPart(value = "signInUserRequest") SignInUserRequest signInUserRequest,
            @RequestPart(value = "profileImage") MultipartFile profileImage
    ) {

        userCreateService.create(userDetail, signInUserRequest, profileImage);

        return Response.success(HttpStatus.OK, "회원가입 성공");
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보 기입")
    @PatchMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> modifyUser(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @Valid @RequestPart("modifyUserRequest") ModifyUserRequest modifyUserRequest,
            @RequestPart(value = "profileImage") @Nullable MultipartFile profileImage
    ) {
        userCreateService.modifyUser(customUserDetail, modifyUserRequest, profileImage);

        return Response.success(HttpStatus.OK, "회원정보 수정 성공");
    }

    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회")
    @GetMapping("/myInfo")
    public Response<FindUserResponse> findMyInfo(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        FindUserResponse findUserResponse = userService.findMyInfo(userDetail);
        return Response.success(HttpStatus.OK, "회원 조회 성공", findUserResponse);
    }

    @Operation(summary = "계정 정보 조회", description = "계정 정보 조회")
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
            @RequestBody List<String> modifyBoardKeyword
    ) {
        userService.modifyBoardKeyword(userDetail, modifyBoardKeyword);

        return Response.success(HttpStatus.OK, "키워드 갱신 완료");
    }
    @Operation(summary = "회원가입시 태그 데이터 반환", description = "회원 추가 정보 가입시 사용할 태그 데이터를 반환합니다.")
    @GetMapping("/tags")
    public Response<TagListDTO> getTagLists(
    ) {
        TagListDTO tagListDTO = userService.getTagsLists();

        return Response.success(HttpStatus.OK, "키워드 조회 완료", tagListDTO);
    }

}
