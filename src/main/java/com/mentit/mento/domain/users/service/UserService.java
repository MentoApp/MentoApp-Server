package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.dotoriToken.service.DotoriTokenService;
import com.mentit.mento.domain.users.dto.request.ModifyUserRequest;
import com.mentit.mento.domain.users.dto.request.SignInUserRequest;
import com.mentit.mento.domain.users.dto.response.FindUserResponse;
import com.mentit.mento.domain.users.entity.*;
import com.mentit.mento.domain.users.repository.*;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

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

    public void create(CustomUserDetail userDetail, SignInUserRequest signInUserRequest, MultipartFile profileImage) {
        Users findUserByUserDetail = getUsers(userDetail);

        if(findUserByUserDetail.getUserStatusTag()!=null){
            throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT);
        }

        // 프로필 이미지 업로드
        //TODO:: 첨부된 이미지가 없을 경우 기본 이미지로 대체하도록 설정하기
        String uploadedFile = null;
        if (profileImage!=null) {
            uploadedFile = s3FileUtilImpl.upload(profileImage);
        }

        // 유저 상태 태그 생성 및 저장 (서비스로 위임)
        UserStatusTag userStatusTag = userStatusTagService.createUserStatusTag(signInUserRequest, findUserByUserDetail);
        userStatusTag = userStatusTagRepository.save(userStatusTag);

        //BoardKeyword 생성 및 저장
        boardKeywordService.createUserBoardKeyword(signInUserRequest.getBoardKeywords(), findUserByUserDetail);

        // DotoriToken 및 관련 상세 정보 생성 (토큰 서비스로 위임)
        dotoriTokenService.createDotoriToken(findUserByUserDetail);

        // 유저 정보 업데이트
        updateUserInformation(findUserByUserDetail, signInUserRequest, uploadedFile, userStatusTag);

    }

    private void updateUserInformation(Users user, SignInUserRequest request, String uploadedFile, UserStatusTag userStatusTag) {
        Users updatedUser = user.toBuilder()
                .job(request.getJob())
                .nickname(request.getNickname())
                .userStatusTag(userStatusTag)
                .profileImage(uploadedFile)
                .simpleIntroduce(request.getSimpleIntroduce())
                .build();

        userRepository.save(updatedUser);
    }

    public void modifyUser(CustomUserDetail customUserDetail,
                           @Valid ModifyUserRequest modifyUserRequest,
                           MultipartFile profileImage) {
        Users findUserByUserDetail = getUsers(customUserDetail);

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
        if(findUserByUserDetail.getUserStatusTag()!=null){
            userStatusTagService.deleteExistingUserStatusTag(findUserByUserDetail);
        }

        // 새로운 UserStatusTag 생성
        UserStatusTag savedTag = userStatusTagService.createUserStatusTag(modifyUserRequest, findUserByUserDetail);

        // 기존 게시판 키워드 삭제
        boardKeywordService.deleteExistingBoardKeywords(findUserByUserDetail);

        //새로운 게시판 키워드 생성
        boardKeywordService.createUserBoardKeyword(modifyUserRequest.getBoardKeywords(), findUserByUserDetail);

        // 유저 정보 업데이트
        updateUser(findUserByUserDetail, modifyUserRequest, uploadedFile, savedTag);
    }



    private void deleteExistingProfileImage(Users user) {
        if (user.getProfileImage() != null && !user.getProfileImage().isBlank()) {
            s3FileUtilImpl.deleteImageFromS3(user.getProfileImage());
        }
    }

    private void updateUser(Users user, ModifyUserRequest modifyUserRequest, String uploadedFile, UserStatusTag savedTag) {
        Users updatedUser = user.toBuilder()
                .nickname(modifyUserRequest.getNickname())
                .profileImage(uploadedFile)
                .simpleIntroduce(modifyUserRequest.getSimpleIntroduce())
                .userStatusTag(savedTag)
                .build();

        userRepository.save(updatedUser);
    }

    public boolean validateNickname(String nickname, CustomUserDetail userDetail) {
        Users findUserByUserDetail = getUsers(userDetail);
        boolean flag;
        boolean isPresent = userRepository.findByNickname(nickname, findUserByUserDetail.getUserId()).isPresent();

        log.info("닉네임 존재 여부 ={}", isPresent);
        flag = isPresent;

        return flag;
    }

    private Users getUsers(CustomUserDetail userDetail) {
        return userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public void deleteSocialMember(Long uuid) {
        Users findUser = getUserById(uuid);

        socialAccessTokenRepository.findByUser(findUser).ifPresent(
                accessToken -> {
                    String socialAccessToken = accessToken.getSocialAccessToken();
                    revokeSocialAccessToken(findUser, socialAccessToken);
                    socialAccessTokenRepository.delete(accessToken);
                }
        );

        userRepository.delete(findUser);
    }

    private void revokeSocialAccessToken(Users findUser, String socialAccessToken) {
        switch (findUser.getAuthType()) {
            case MEMBER_KAKAO -> oAuth2RevokeService.revokeKakao(socialAccessToken);
            case MEMBER_NAVER -> oAuth2RevokeService.revokeNaver(socialAccessToken);
        }
    }

    private Users getUserById(Long id) {
        refreshTokenRepository.getRefreshTokenByMemberId(id).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN)
        );
        return userRepository.findById(id).
                orElseThrow(
                        () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
                );
    }

    public void logout(String refreshToken, CustomUserDetail userDetail) {
        jwtService.deleteRefreshTokenDB(refreshToken);
    }


    public JwtToken reissueToken(String refreshToken) {
        return jwtService.reissueTokenByRefreshToken(refreshToken);
    }

    public FindUserResponse findMyInfo(CustomUserDetail userDetail) {
        Users findUserByUserDetail = getUsers(userDetail);
        UserStatusTag userStatusTag = findUserByUserDetail.getUserStatusTag();

        List<String> boardKeywordList = boardKeywordService.getBoardKeywords(findUserByUserDetail);

        List<String> baseTagList = userStatusTagService.getBaseTags(userStatusTag);

        List<String> currentJobStatusList = userStatusTagService.getCurrentJobStatuses(userStatusTag);

        List<String> myStatusTagsList = userStatusTagService.getMyStatusTags(userStatusTag);

        return FindUserResponse.builder()
                .id(userDetail.getId())
                .name(findUserByUserDetail.getName())
                .phoneNumber(findUserByUserDetail.getPhoneNumber())
                .simpleIntroduce(findUserByUserDetail.getSimpleIntroduce())
                .nickname(findUserByUserDetail.getNickname())
                .profileImage(findUserByUserDetail.getProfileImage())
                .dotoriTokenAmount(findUserByUserDetail.getDotoriToken().getCount())
                .boardKeywordList(boardKeywordList)
                .currentJobStatus(currentJobStatusList)
                .baseTags(baseTagList)
                .corporateForm(userStatusTag.getCorporateForm().getKoreanValue())
                .myStatus(myStatusTagsList)
                .personalHistory(userStatusTag.getMyCareerTags().getMyCareerTags().getDescription())
                .userJob(findUserByUserDetail.getJob().getKoreanValue())
                .build();
    }

    public String getRefreshToken(Long uuid) {
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByMemberId(uuid).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return refreshToken.getRefreshToken();
    }
}
