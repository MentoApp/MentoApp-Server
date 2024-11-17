package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.dotoriToken.service.DotoriTokenService;
import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.domain.dto.request.ModifyUser;
import com.mentit.mento.domain.users.domain.dto.request.SignInUser;
import com.mentit.mento.domain.users.domain.dto.response.FindUserAccountResponse;
import com.mentit.mento.domain.users.domain.dto.response.FindUserResponse;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.*;
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
public class UserService {

    private final UserRepositoryImpl userRepositoryImpl;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2RevokeService oAuth2RevokeService;
    private final JwtService jwtService;
    private final UserStatusTagRepositoryImpl userStatusTagRepositoryImpl;
    private final S3FileUtilImpl s3FileUtilImpl;
    private final DotoriTokenService dotoriTokenService;
    private final UserStatusTagService userStatusTagService;
    private final BoardKeywordService boardKeywordService;

    public void create(CustomUserDetail userDetail, SignInUser signInUser, MultipartFile profileImage) {
        UsersEntity findUserByUserDetail = getUsers(userDetail);

        if(findUserByUserDetail.getUserStatusTagEntity()!=null){
            throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT);
        }

        // 프로필 이미지 업로드
        //TODO:: 첨부된 이미지가 없을 경우 기본 이미지로 대체하도록 설정하기
        String uploadedFile = null;
        if (profileImage!=null) {
            uploadedFile = s3FileUtilImpl.upload(profileImage);
        }

        // 유저 상태 태그 생성 및 저장 (서비스로 위임)
        UserStatusTagEntity userStatusTagEntity = userStatusTagService.updateUserStatusTag(signInUser, findUserByUserDetail);
        userStatusTagEntity = userStatusTagRepositoryImpl.save(userStatusTagEntity);

        //BoardKeyword 생성 및 저장
        boardKeywordService.createUserBoardKeyword(signInUser.getBoardKeywordEnums(), findUserByUserDetail);

        // DotoriToken 및 관련 상세 정보 생성 (토큰 서비스로 위임)
        dotoriTokenService.createDotoriToken(findUserByUserDetail);

        // 유저 정보 업데이트
        updateUserInformation(findUserByUserDetail, signInUser, uploadedFile, userStatusTagEntity);

    }

    private void updateUserInformation(UsersEntity user, SignInUser request, String uploadedFile, UserStatusTagEntity userStatusTagEntity) {
        UsersEntity updatedUser = user.toBuilder()
                .job(request.getJob())
                .nickname(request.getNickname())
                .userStatusTagEntity(userStatusTagEntity)
                .profileImage(uploadedFile)
                .simpleIntroduce(request.getSimpleIntroduce())
                .isNewUser(false)
                .build();

        userRepositoryImpl.save(updatedUser);
    }

    public void modifyUser(CustomUserDetail customUserDetail,
                           @Valid ModifyUser modifyUser,
                           MultipartFile profileImage) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        if (findUserByUserDetail.getProfileImage()!=null) {
            deleteExistingProfileImage(findUserByUserDetail);
        }

        String uploadedFile = null;
        if(findUserByUserDetail.getProfileImage()!=null){
            s3FileUtilImpl.deleteImageFromS3(findUserByUserDetail.getProfileImage());
        }
        if (profileImage != null) {
            uploadedFile = s3FileUtilImpl.upload(profileImage);
        }

        // 기존 태그 삭제
        if(findUserByUserDetail.getUserStatusTagEntity()!=null){
            userStatusTagService.deleteExistingUserStatusTag(findUserByUserDetail);
        }

        // 새로운 UserStatusTag 생성
        UserStatusTagEntity savedTag = userStatusTagService.createUserStatusTag(modifyUser, findUserByUserDetail);

        // 기존 게시판 키워드 삭제
        boardKeywordService.deleteExistingBoardKeywords(findUserByUserDetail);

        //새로운 게시판 키워드 생성
        boardKeywordService.createUserBoardKeyword(modifyUser.getBoardKeywordEnums(), findUserByUserDetail);

        // 유저 정보 업데이트
        updateUser(findUserByUserDetail, modifyUser, uploadedFile, savedTag);
    }

    private void deleteExistingProfileImage(UsersEntity user) {
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

        userRepositoryImpl.save(updatedUser);
    }

    public boolean validateNickname(String nickname, CustomUserDetail userDetail) {
        UsersEntity findUserByUserDetail = getUsers(userDetail);

        boolean isPresent = userRepositoryImpl.findByNickname(nickname, findUserByUserDetail.getUserId()).isPresent();

        log.info("닉네임 존재 여부 ={}", isPresent);

        if(!isPresent){
            if (nickname.length() < 2) {
                throw new MemberException(ExceptionCode.TOO_SHORT_NICKNAME);
            } else if (nickname.length() > 10) {
                throw new MemberException(ExceptionCode.TOO_LONG_NICKNAME);
            }
            if (!nickname.matches("^[a-zA-Z0-9가-힣]+$")) {
                throw new MemberException(ExceptionCode.NICKNAME_PATTERN_INVALIDATION);
            }
        }


        return !isPresent;
    }

    private UsersEntity getUsers(CustomUserDetail userDetail) {
        return userRepositoryImpl.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public void deleteSocialMember(Long uuid) {
        UsersEntity findUser = getUserById(uuid);

        socialAccessTokenRepository.findByUser(findUser).ifPresent(
                accessToken -> {
                    String socialAccessToken = accessToken.getSocialAccessToken();
                    revokeSocialAccessToken(findUser, socialAccessToken);
                    socialAccessTokenRepository.delete(accessToken);
                }
        );

        userRepositoryImpl.delete(findUser);
    }

    private void revokeSocialAccessToken(UsersEntity findUser, String socialAccessToken) {
        switch (findUser.getAuthType()) {
            case MEMBER_KAKAO -> oAuth2RevokeService.revokeKakao(socialAccessToken);
            case MEMBER_NAVER -> oAuth2RevokeService.revokeNaver(socialAccessToken);
        }
    }

    private UsersEntity getUserById(Long id) {
        refreshTokenRepository.getRefreshTokenByMemberId(id).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN)
        );
        return userRepositoryImpl.findById(id).
                orElseThrow(
                        () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
                );
    }

    public void logout(String refreshToken) {
        jwtService.deleteRefreshTokenDB(refreshToken);

        // 쿠키에서 refreshToken 삭제
        Cookie cookie = new Cookie("refreshToken", null); // 쿠키 값을 null로 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // 쿠키의 경로 설정 (일반적으로 루트 경로로 설정)
        cookie.setMaxAge(0); // 쿠키의 유효기간을 0으로 설정하여 즉시 삭제

    }


    public JwtToken reissueToken(String refreshToken) {
        return jwtService.reissueTokenByRefreshToken(refreshToken);
    }

    @Transactional
    public FindUserResponse findMyInfo(CustomUserDetail userDetail) {
        UsersEntity findUserByUserDetail = getUsers(userDetail);
        UserStatusTagEntity userStatusTagEntity = findUserByUserDetail.getUserStatusTagEntity();

        List<String> boardKeywordList = boardKeywordService.getBoardKeywords(findUserByUserDetail);

//        List<String> baseTagList = userStatusTagService.getBaseTags(userStatusTag);

//        List<String> currentJobStatusList = userStatusTagService.getCurrentJobStatuses(userStatusTag);

        List<String> myStatusTagsList = userStatusTagService.getMyStatusTags(userStatusTagEntity);

        return FindUserResponse.builder()
                .id(userDetail.getId())
                .name(findUserByUserDetail.getName())
                .phoneNumber(findUserByUserDetail.getPhoneNumber())
                .simpleIntroduce(findUserByUserDetail.getSimpleIntroduce())
                .nickname(findUserByUserDetail.getNickname())
                .profileImage(findUserByUserDetail.getProfileImage())
                .dotoriTokenAmount(findUserByUserDetail.getDotoriToken().getCount())
                .boardKeywordList(boardKeywordList)
//                .currentJobStatus(currentJobStatusList)
//                .baseTags(baseTagList)
                .corporateForm(userStatusTagEntity.getCorporateFormEnum().getKoreanValue())
                .myStatus(myStatusTagsList)
                .personalHistory(userStatusTagEntity.getMyCareerTags().getMyCareerTagsEnum().getDescription())
                .userJob(findUserByUserDetail.getJob().getKoreanValue())
                .build();
    }

    public String getRefreshToken(Long uuid) {
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByMemberId(uuid).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return refreshToken.getRefreshToken();
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
}
