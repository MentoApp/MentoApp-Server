package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.dotoriToken.constant.TradeType;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenRepository;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenUsageDetailsRepository;
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
    private final BoardKeywordRepository boardKeywordRepository;

    public void create(CustomUserDetail userDetail, SignInUserRequest signInUserRequest, MultipartFile profileImage) {
        Users findUserByUserDetail = getUsers(userDetail);

        if(findUserByUserDetail.getUserStatusTag()!=null){
            throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT);
        }

        // 닉네임 중복 검사
        checkNicknameDuplication(signInUserRequest.getNickname(), findUserByUserDetail);

        // 프로필 이미지 업로드
        //TODO:: 첨부된 이미지가 없을 경우 기본 이미지로 대체하도록 설정하기
        String uploadedFile = null;
        if (profileImage!=null) {
            uploadedFile = uploadProfileImage(profileImage);
        }

        // UserStatusTag 생성 및 저장
        UserStatusTag userStatusTag = createUserStatusTag(signInUserRequest, findUserByUserDetail);
        UserStatusTag savedUserStatus = userStatusTagRepository.save(userStatusTag);

        //BoardKeyword 생성 및 저장
        createUserBoardKeyword(signInUserRequest, findUserByUserDetail);

        // DotoriToken 및 관련 상세 정보 생성
        createDotoriToken(findUserByUserDetail);

        // 유저 정보 업데이트
        updateUserInformation(findUserByUserDetail, signInUserRequest, uploadedFile, savedUserStatus);

    }

    private void createUserBoardKeyword(SignInUserRequest request, Users findUserByUserDetail) {
        request.getBoardKeywords().forEach(
                boardKeyword -> log.info("boardKeyword={}", boardKeyword)
        );
        request.getBoardKeywords()
                .forEach(keyword -> {
                            BoardKeywordEntity boardKeywordEntity = BoardKeywordEntity.builder()
                                    .boardKeyword(keyword)
                                    .users(findUserByUserDetail)
                                    .build();
                            BoardKeywordEntity savedBoardKeyWordEntity = boardKeywordRepository.save(boardKeywordEntity);
                            findUserByUserDetail.getBoardKeywords().add(savedBoardKeyWordEntity);
                        }
                );
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
                .map(tag ->
                        BaseTagEntity.builder()
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
                .corporateForm(request.getCorporateForm())
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

        userRepository.save(updatedUser);
    }

    public void modifyUser(CustomUserDetail customUserDetail,
                           @Valid ModifyUserRequest modifyUserRequest,
                           MultipartFile profileImage) {
        Users findUserByUserDetail = getUsers(customUserDetail);

        if (!findUserByUserDetail.getProfileImage().isEmpty()) {
            deleteExistingProfileImage(findUserByUserDetail);
        }

        String uploadedFile = null;
        if (profileImage != null) {
            uploadedFile = uploadProfileImage(profileImage);
        }

        checkNicknameDuplication(modifyUserRequest.getNickname(), findUserByUserDetail);

        // 기존 태그 삭제
        deleteExistingUserStatusTag(findUserByUserDetail);

        // 새로운 UserStatusTag 생성
        UserStatusTag savedTag = createUserStatusTag(modifyUserRequest, findUserByUserDetail);

        // 기존 게시판 키워드 삭제
        deleteExistingBoardKeywords(findUserByUserDetail);

        //새로운 게시판 키워드 생성
        createUserBoardKeyword(modifyUserRequest, findUserByUserDetail);

        // 유저 정보 업데이트
        updateUser(findUserByUserDetail, modifyUserRequest, uploadedFile, savedTag);
    }

    private void createUserBoardKeyword(ModifyUserRequest request, Users findUserByUserDetail) {
        request.getBoardKeywords().forEach(
                boardKeyword -> log.info("boardKeyword={}", boardKeyword)
        );
        request.getBoardKeywords()
                .forEach(keyword -> {
                            BoardKeywordEntity boardKeywordEntity = BoardKeywordEntity.builder()
                                    .boardKeyword(keyword)
                                    .users(findUserByUserDetail)
                                    .build();
                            BoardKeywordEntity savedBoardKeyWordEntity = boardKeywordRepository.save(boardKeywordEntity);
                            findUserByUserDetail.getBoardKeywords().add(savedBoardKeyWordEntity);
                        }
                );
    }

    private void deleteExistingBoardKeywords(Users findUserByUserDetail) {
        boardKeywordRepository.deleteAllByUsers(findUserByUserDetail);
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
                .corporateForm(request.getCorporateForm())
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
        boolean flag;
        boolean isPresent = userRepository.findByNickname(nickname, findUserByUserDetail.getUserId()).isPresent();

        log.info("isPresent={}", isPresent);
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

    public FindUserResponse findMyInfo(CustomUserDetail userDetail) {
        Users findUserByUserDetail = getUsers(userDetail);
        UserStatusTag userStatusTag = findUserByUserDetail.getUserStatusTag();

        List<String> boardKeywordList = getBoardKeywords(findUserByUserDetail);

        List<String> baseTagList = getBaseTags(userStatusTag);

        List<String> currentJobStatusList = getCurrentJobStatuses(userStatusTag);

        List<String> myStatusTagsList = getMyStatusTags(userStatusTag);

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
                .personalHistory(userStatusTag.getPersonalHistory())
                .userJob(findUserByUserDetail.getJob().getKoreanValue())
                .build();
    }

    private static List<String> getMyStatusTags(UserStatusTag userStatusTag) {
        List<String> myStatusTagsList = new ArrayList<>();

        userStatusTag.getMyStatus().forEach(
                myStatus -> {
                    myStatusTagsList.add(myStatus.getMyStatusTag().getDescription());
                }
        );
        return myStatusTagsList;
    }

    private static List<String> getCurrentJobStatuses(UserStatusTag userStatusTag) {
        List<String> currentJobStatusList = new ArrayList<>();

        userStatusTag.getCurrentJobStatus().forEach(
                currentJobStatus -> currentJobStatusList.add(currentJobStatus.getCurrentJobStatus().getDescription())
        );
        return currentJobStatusList;
    }

    private static List<String> getBaseTags(UserStatusTag userStatusTag) {
        List<String> baseTagList = new ArrayList<>();
        userStatusTag.getBaseTags().forEach(
                i -> baseTagList.add(i.getBaseTag().getDescription())
        );
        return baseTagList;
    }

    private static List<String> getBoardKeywords(Users findUserByUserDetail) {
        List<String> boardKeywordList = new ArrayList<>();

        findUserByUserDetail.getBoardKeywords().forEach(
                boardKeyword -> {
                    boardKeywordList.add(boardKeyword.getBoardKeyword().getKoreanValue());
                }
        );
        return boardKeywordList;
    }
}
