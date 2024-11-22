package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.dotoriToken.service.DotoriTokenService;
import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.dto.request.ModifyUser;
import com.mentit.mento.domain.users.domain.dto.request.SignInUser;
import com.mentit.mento.domain.users.domain.dto.response.FindUserAccountResponse;
import com.mentit.mento.domain.users.domain.dto.response.FindUserResponse;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.domain.users.service.port.UserStatusTagRepository;
import com.mentit.mento.global.authToken.entity.RefreshToken;
import com.mentit.mento.global.authToken.repository.RefreshTokenRepository;
import com.mentit.mento.global.authToken.repository.SocialAccessTokenRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.jwt.service.JwtService;
import com.mentit.mento.global.oauth.service.OAuth2RevokeService;
import com.mentit.mento.global.s3.S3FileUtilImpl;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserCreateService {

    private final UserRepository userRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2RevokeService oAuth2RevokeService;
    private final JwtService jwtService;
    private final UserStatusTagRepository userStatusTagRepository;
    private final S3FileUtilImpl s3FileUtilImpl;
    private final DotoriTokenService dotoriTokenService;
    private final UserStatusTagService userStatusTagService;
    private final BoardKeywordService boardKeywordService;

    @Transactional
    public UsersEntity create(CustomUserDetail userDetail, SignInUser signInRequest, MultipartFile profileImage) {
        UsersEntity usersEntity = getUsers(userDetail);

        if (usersEntity.getUserStatusTagEntity() != null) {
            throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT);
        }

        // 프로필 이미지 업로드
        String uploadedFile = null;
        if (profileImage != null) {
            uploadedFile = s3FileUtilImpl.upload(profileImage);
        }

        // 유저 상태 태그 생성 및 저장
        UserStatusTagEntity userStatusTagEntity = userStatusTagService.create(signInRequest, usersEntity);

        //BoardKeyword 생성 및 저장
        boardKeywordService.createUserBoardKeyword(signInRequest.getBoardKeywordEnums(), usersEntity);

        // DotoriToken 및 관련 상세 정보 생성 (토큰 서비스로 위임)
        dotoriTokenService.createDotoriToken(usersEntity);

        // 유저 정보 업데이트
        updateUserInformation(usersEntity, signInRequest, uploadedFile, userStatusTagEntity);

        return usersEntity;
    }

    public UsersEntity modifyUser(CustomUserDetail customUserDetail,
                           @Valid ModifyUser modifyUserRequest,
                           MultipartFile profileImage) {
        UsersEntity usersEntity = getUsers(customUserDetail);

        if (usersEntity.getProfileImage() != null) {
            deleteExistingProfileImage(usersEntity.to());
        }

        String uploadedFile = null;
        if (profileImage != null) {
            uploadedFile = s3FileUtilImpl.upload(profileImage);
        }

        // 기존 태그 삭제
        if (usersEntity.getUserStatusTagEntity() != null) {
            usersEntity = userStatusTagService.delete(usersEntity); //삭제 후 초기화한 유저 반환
        }

        // 새로운 UserStatusTag 생성
        UserStatusTagEntity userStatusTagEntity = userStatusTagService.update(modifyUserRequest, usersEntity);


        // 기존 게시판 키워드 삭제
        boardKeywordService.deleteExistingBoardKeywords(usersEntity);

        //새로운 게시판 키워드 생성
        boardKeywordService.createUserBoardKeyword(modifyUserRequest.getBoardKeywordEnums(), usersEntity);

        // 유저 정보 업데이트
        updateUser(usersEntity, modifyUserRequest, uploadedFile, userStatusTagEntity);

        return usersEntity;
    }

    public FindUserAccountResponse findMyAccountInfo(CustomUserDetail userDetail) {
        UsersEntity findUserByUserDetail = getUsers(userDetail);

        return FindUserAccountResponse.builder()
                .name(findUserByUserDetail.getName())
                .email(findUserByUserDetail.getEmail())
                .phoneNumber(findUserByUserDetail.getPhoneNumber())
                .platform(AuthType.fromEnumValue(findUserByUserDetail.getAuthType()))
                .build();
    }

    private void deleteExistingProfileImage(Users user) {
        if (user.getProfileImage() != null && !user.getProfileImage().isBlank()) {
            s3FileUtilImpl.deleteImageFromS3(user.getProfileImage());
        }
    }

    private void updateUser(UsersEntity user, ModifyUser modifyUser, String uploadedFile, UserStatusTagEntity savedTag) {
        UsersEntity updatedUser = user.toBuilder()
                .job(modifyUser.getJob())
                .nickname(modifyUser.getNickname())
                .profileImage(uploadedFile)
                .simpleIntroduce(modifyUser.getSimpleIntroduce())
                .userStatusTagEntity(savedTag)
                .build();

        userRepository.save(updatedUser);
    }

    private void updateUserInformation(UsersEntity user, SignInUser request, String uploadedFile, UserStatusTagEntity userStatusTag) {
        user.setJob(request.getJob());
        user.setNickname(request.getNickname());
        user.setUserStatusTagEntity(userStatusTag);
        user.setProfileImage(uploadedFile);
        user.setSimpleIntroduce(request.getSimpleIntroduce());
        user.setNewUser(false);

        // 기존 객체를 수정하여 저장
        userRepository.save(user);
    }

    private UsersEntity getUsers(CustomUserDetail userDetail) {
        return userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    private UsersEntity getUserById(Long id) {
        refreshTokenRepository.getRefreshTokenByMemberId(id).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN)
        );
        return userRepository.findById(id).
                orElseThrow(
                        () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
                );
    }


}
