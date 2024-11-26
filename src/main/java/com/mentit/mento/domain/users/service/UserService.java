package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.users.constant.BoardKeywordEnum;
import com.mentit.mento.domain.users.domain.dto.response.FindUserResponse;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserStatusTagService userStatusTagService;
    private final BoardKeywordService boardKeywordService;

    public boolean validateNickname(String nickname, CustomUserDetail userDetail) {
        UsersEntity findUserByUserDetail = getUsers(userDetail);
        log.info(findUserByUserDetail.getNickname() + "닉네임 존재");

        if (nickname.equals(findUserByUserDetail.getNickname())) {
            log.info("현재 유저가 사용하는 닉네임");
            return true;
        }
        //
        boolean isPresent = userRepository.findByNickname(nickname).isPresent();


        if (!isPresent) {
            if (nickname.length() < 2) {
                throw new MemberException(ExceptionCode.TOO_SHORT_NICKNAME);
            } else if (nickname.length() > 10) {
                throw new MemberException(ExceptionCode.TOO_LONG_NICKNAME);
            }
            if (!nickname.matches("^[a-zA-Z0-9가-힣]+$")) {
                throw new MemberException(ExceptionCode.NICKNAME_PATTERN_INVALIDATION);
            }
        }

        log.info("닉네임 존재 여부 ={}", isPresent);
        return !isPresent;
    }

    @Transactional
    public FindUserResponse findMyInfo(CustomUserDetail userDetail) {
        UsersEntity findUserByUserDetail = getUsers(userDetail);
        UserStatusTagEntity userStatusTag = findUserByUserDetail.getUserStatusTagEntity();

        List<String> boardKeywordList = boardKeywordService.getBoardKeywords(findUserByUserDetail);

        List<String> myStatusTagsList = userStatusTagService.find(userStatusTag);

        return FindUserResponse.builder()
                .id(userDetail.getId())
                .name(findUserByUserDetail.getName())
                .phoneNumber(findUserByUserDetail.getPhoneNumber())
                .simpleIntroduce(findUserByUserDetail.getSimpleIntroduce())
                .nickname(findUserByUserDetail.getNickname())
                .profileImage(findUserByUserDetail.getProfileImage())
                .dotoriTokenAmount(findUserByUserDetail.getDotoriTokenEntity()!=null? findUserByUserDetail.getDotoriTokenEntity().getCount() : 0)
                .boardKeywordList(boardKeywordList)
                .corporateForm(userStatusTag.getCorporateFormEnum().getKoreanValue())
                .myStatus(myStatusTagsList)
                .personalHistory(userStatusTag.getMyCareerTags().getMyCareerTagsEnum().getDescription())
                .userJob(findUserByUserDetail.getJob().getKoreanValue())
                .build();
    }

    private UsersEntity getUsers(CustomUserDetail userDetail) {
        return userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public void modifyBoardKeyword(CustomUserDetail userDetail, List<String> modifyBoardKeyword) {
        UsersEntity usersEntity = getUsers(userDetail);

        boardKeywordService.deleteExistingBoardKeywords(usersEntity);
        List<BoardKeywordEnum> boardKeywordEnums = modifyBoardKeyword.stream().map(
                keyword -> {
                    return BoardKeywordEnum.fromKoreanValue(keyword);
                }
        ).toList();
        List<BoardKeywordEntity> userBoardKeyword = boardKeywordService.createUserBoardKeyword(boardKeywordEnums, usersEntity);

        usersEntity.setBoardKeywords(userBoardKeyword);

        userRepository.save(usersEntity);
    }
}
