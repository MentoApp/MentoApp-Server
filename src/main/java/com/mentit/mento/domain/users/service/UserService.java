package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.dotoriToken.constant.TradeType;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenRepository;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenUsageDetailsRepository;
import com.mentit.mento.domain.users.dto.request.ModifyUserRequest;
import com.mentit.mento.domain.users.dto.request.SignInUserRequest;
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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
    private final DotoriTokenRepository dotoriTokenRepository;
    private final DotoriTokenUsageDetailsRepository dotoriTokenUsageDetailsRepository;
    private final MyStatusTagsEntityRepository myStatusTagsEntityRepository;
    private final CurrentJobStatusEntityRepository currentJobStatusEntityRepository;
    private final BaseEntityRepository baseEntityRepository;

    public void create(CustomUserDetail userDetail, SignInUserRequest signInUserRequest, MultipartFile profileImage) {
        Users findUserByUserDetail = getUsers(userDetail);

        // 프로필 이미지 업로드
        String uploadedFile = uploadProfileImage(profileImage);

        // 닉네임 중복 검사
        checkNicknameDuplication(signInUserRequest.getNickname(), findUserByUserDetail);

        // UserStatusTag 생성 및 저장
        UserStatusTag userStatusTag = createUserStatusTag(signInUserRequest, findUserByUserDetail);
        UserStatusTag savedUserStatus = userStatusTagRepository.save(userStatusTag);

        // DotoriToken 및 관련 상세 정보 생성
        createDotoriToken(findUserByUserDetail);

        // 유저 정보 업데이트
        updateUserInformation(findUserByUserDetail, signInUserRequest, uploadedFile, savedUserStatus);

        log.info("UserInformation={}", List.of(findUserByUserDetail.getNickname(), uploadedFile));
    }

    private void checkNicknameDuplication(String nickname, Users findUser) {
        boolean isExistNickname = validateNickname(nickname, findUser);
        if (isExistNickname) {
            throw new MemberException(ExceptionCode.NICKNAME_ALREADY_EXISTS);
        }
        log.info("닉네임 중복 여부: {}", isExistNickname);
    }

    private UserStatusTag createUserStatusTag(SignInUserRequest request, Users user) {
        List<BaseTagEntity> baseTagEntities = request.getBaseTags().stream()
                .map(tag -> BaseTagEntity.builder()
                        .baseTag(tag)
                        .build())
                .toList();

        List<MyStatusTagsEntity> myStatusEntities = request.getMyStatus().stream()
                .map(status -> MyStatusTagsEntity.builder()
                        .myStatusTag(status)
                        .build())
                .toList();

        List<CurrentJobStatusEntity> currentJobStatusEntities = request.getCurrentJobStatus().stream()
                .map(status -> CurrentJobStatusEntity.builder()
                        .currentJobStatus(status)
                        .build())
                .toList();

        UserStatusTag userStatusTag = UserStatusTag.builder()
                .baseTags(new ArrayList<>(baseTagEntities)) // 새로운 ArrayList로 변경
                .coporateForm(request.getCoporateForm())
                .currentJobStatus(new ArrayList<>(currentJobStatusEntities)) // 새로운 ArrayList로 변경
                .personalHistory(request.getPersonalHistory())
                .myStatus(new ArrayList<>(myStatusEntities)) // 새로운 ArrayList로 변경
                .users(user)
                .build();

         userStatusTagRepository.save(userStatusTag);


        // 각 엔티티에 UserStatusTag 설정
        baseTagEntities.forEach(baseTag -> {
            baseTag = baseTag.toBuilder().userStatusTag(userStatusTag).build(); // toBuilder() 사용
            // 태그 엔티티 저장
            baseEntityRepository.save(baseTag); // 적절한 repository 사용
        });

        myStatusEntities.forEach(myStatus -> {
            myStatus = myStatus.toBuilder().userStatusTag(userStatusTag).build(); // toBuilder() 사용
            // 태그 엔티티 저장
            myStatusTagsEntityRepository.save(myStatus); // 적절한 repository 사용
        });

        currentJobStatusEntities.forEach(jobStatus -> {
            jobStatus = jobStatus.toBuilder().userStatusTag(userStatusTag).build(); // toBuilder() 사용
            // 태그 엔티티 저장
            currentJobStatusEntityRepository.save(jobStatus); // 적절한 repository 사용
        });

        return userStatusTag; // 생성한 UserStatusTag를 반환
    }

    private void createDotoriToken(Users findUserByUserDetail) {
        DotoriToken dotoriToken = DotoriToken.builder()
                .users(findUserByUserDetail)
                .count(5)
                .build();
        dotoriTokenRepository.save(dotoriToken);

        DotoriTokenUsageDetails dotoriTokenUsageDetails = DotoriTokenUsageDetails.builder()
                .receiver(findUserByUserDetail)
                .tradeAmount(5)
                .tradeType(TradeType.EARN)
                .dotoriToken(dotoriToken)
                .build();
        dotoriTokenUsageDetailsRepository.save(dotoriTokenUsageDetails);
    }

    private void updateUserInformation(Users user, SignInUserRequest request, String uploadedFile, UserStatusTag userStatusTag) {
        Users updatedUser = user.toBuilder()
                .job(request.getJob())
                .nickname(request.getNickname())
                .userStatusTag(userStatusTag)
                .profileImage(uploadedFile)
                .simpleIntroduce(request.getSimpleIntroduce())
                .build();

        log.info("test={}", updatedUser.getUserStatusTag().getUsers().getUserId());
        userRepository.save(updatedUser);
    }

    public void modifyUser(CustomUserDetail customUserDetail,
                           @Valid ModifyUserRequest modifyUserRequest,
                           MultipartFile profileImage) {
        Users findUserByUserDetail = getUsers(customUserDetail);


        deleteExistingProfileImage(findUserByUserDetail);

        String uploadedFile = uploadProfileImage(profileImage);
        checkNicknameDuplication(modifyUserRequest.getNickname(), findUserByUserDetail);

        // 기존 태그 삭제
        deleteExistingUserStatusTag(findUserByUserDetail);

        // 새로운 UserStatusTag 생성
        UserStatusTag savedTag = createUserStatusTag(modifyUserRequest, findUserByUserDetail);

        // 유저 정보 업데이트
        updateUser(findUserByUserDetail, modifyUserRequest, uploadedFile, savedTag);
    }

    private void deleteExistingProfileImage(Users user) {
        if (user.getProfileImage() != null && !user.getProfileImage().isBlank()) {
            s3FileUtilImpl.deleteImageFromS3(user.getProfileImage());
        }
    }

    private String uploadProfileImage(MultipartFile profileImage) {
        String uploadedFile = s3FileUtilImpl.upload(profileImage);
        log.info("uploadedFile = {}", uploadedFile);
        return uploadedFile;
    }

    private void deleteExistingUserStatusTag(Users user) {
        UserStatusTag findUserStatusTag = userStatusTagRepository.findByUsers(user).orElseThrow(
                () -> new MemberException(ExceptionCode.CANT_FIND_USERSTATUS)
        );

        Users modifiedUser = user.toBuilder()
                .userStatusTag(null)
                .build();
        userRepository.save(modifiedUser);

        log.info("userStatusTag = {}", findUserStatusTag.getUserStatusTagId());

            userStatusTagRepository.delete(findUserStatusTag);
    }

    private UserStatusTag createUserStatusTag(ModifyUserRequest request, Users user) {
        List<BaseTagEntity> baseTagEntities = request.getBaseTags().stream()
                .map(tag -> BaseTagEntity.builder()
                        .baseTag(tag)
                        .build())
                .toList();

        List<MyStatusTagsEntity> myStatusEntities = request.getMyStatus().stream()
                .map(status -> MyStatusTagsEntity.builder()
                        .myStatusTag(status)
                        .build())
                .toList();

        List<CurrentJobStatusEntity> currentJobStatusEntities = request.getCurrentJobStatus().stream()
                .map(status -> CurrentJobStatusEntity.builder()
                        .currentJobStatus(status)
                        .build())
                .toList();

        UserStatusTag userStatusTag = UserStatusTag.builder()
                .baseTags(new ArrayList<>(baseTagEntities)) // 새로운 ArrayList로 변경
                .coporateForm(request.getCoporateForm())
                .currentJobStatus(new ArrayList<>(currentJobStatusEntities)) // 새로운 ArrayList로 변경
                .personalHistory(request.getPersonalHistory())
                .myStatus(new ArrayList<>(myStatusEntities)) // 새로운 ArrayList로 변경
                .users(user)
                .build();

        userStatusTagRepository.save(userStatusTag);


        // 각 엔티티에 UserStatusTag 설정
        baseTagEntities.forEach(baseTag -> {
            baseTag = baseTag.toBuilder().userStatusTag(userStatusTag).build(); // toBuilder() 사용
            // 태그 엔티티 저장
            baseEntityRepository.save(baseTag); // 적절한 repository 사용
        });

        myStatusEntities.forEach(myStatus -> {
            myStatus = myStatus.toBuilder().userStatusTag(userStatusTag).build(); // toBuilder() 사용
            // 태그 엔티티 저장
            myStatusTagsEntityRepository.save(myStatus); // 적절한 repository 사용
        });

        currentJobStatusEntities.forEach(jobStatus -> {
            jobStatus = jobStatus.toBuilder().userStatusTag(userStatusTag).build(); // toBuilder() 사용
            // 태그 엔티티 저장
            currentJobStatusEntityRepository.save(jobStatus); // 적절한 repository 사용
        });

        return userStatusTag; // 생성한 UserStatusTag를 반환
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

    private boolean validateNickname(String nickname, Users findUserByUserDetail) {
        boolean flag = false;
        if (findUserByUserDetail.getNickname().equals(nickname)) {
            flag = true;
        }
        if (userRepository.findByNickname(nickname, findUserByUserDetail.getUserId()).isPresent()) {
            flag = true;
        }

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

    public String getRefreshToken(Long id) {
        Users findUser = getUserById(id);
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByMemberId(id).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return refreshToken.getRefreshToken();
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

    public JwtToken reissueToken(String refreshToken) {
        return jwtService.reissueTokenByRefreshToken(refreshToken);
    }
}
