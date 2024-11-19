package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.users.constant.CorporateFormEnum;
import com.mentit.mento.domain.users.domain.MyCareerTags;
import com.mentit.mento.domain.users.domain.MyStatusTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.dto.request.ModifyUser;
import com.mentit.mento.domain.users.domain.dto.request.SignInUser;
import com.mentit.mento.domain.users.domain.entity.MyCareerTagsEntity;
import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.MyCareerTagsEntityRepository;
import com.mentit.mento.domain.users.service.port.MyStatusTagsEntityRepository;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.domain.users.service.port.UserStatusTagRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserStatusTagService {

    private final UserStatusTagRepository userStatusTagRepository;
    private final UserRepository userRepository;
    private final MyStatusTagsEntityRepository myStatusTagsEntityRepository;
    private final MyCareerTagsEntityRepository myCareerTagsEntityRepository;

    @Transactional
    public UserStatusTag create(SignInUser request, Users user) {
        
        // 먼저 Users 엔티티 저장
        Users savedUser = userRepository.save(user);

        // MyStatusTags 생성 및 null 체크
        List<MyStatusTags> myStatusEntities = request.getMyStatus() != null 
            ? request.getMyStatus().stream()
                .filter(Objects::nonNull)
                .map(status -> MyStatusTags.builder()
                    .myStatusTag(status)
                    .build())
                .toList()
            : new ArrayList<>();

        // MyCareerTags 생성 및 null 체크
        MyCareerTags myCareerTags = request.getPersonalHistory() != null 
            ? MyCareerTags.builder()
                .myCareerTagsEnum(request.getPersonalHistory())
                .build()
            : null;

        if (myCareerTags != null) {
            myCareerTagsEntityRepository.save(myCareerTags);
        }

        // UserStatusTag 생성
        UserStatusTag userStatusTag = UserStatusTag.builder()
            .corporateFormEnum(request.getCorporateFormEnum())
            .myCareerTags(myCareerTags != null ? myCareerTags : null)
            .myStatus(myStatusEntities)
            .usersEntity(user)
            .build();

        userStatusTagRepository.save(userStatusTag);

        // 연관 관계 설정
        if (!myStatusEntities.isEmpty()) {
            myStatusEntities.forEach(myStatus -> myStatusTagsEntityRepository.save(myStatus, userStatusTag));
        }
        if (myCareerTags != null) {
            myCareerTagsEntityRepository.save(myCareerTags, userStatusTag);
        }

        return userStatusTag;
    }

    public void delete(Users user) {
        //유저의 모든 태그 조회
        UserStatusTag findUserStatusTag = userStatusTagRepository.findByUsers(user).orElseThrow(
                () -> new MemberException(ExceptionCode.CANT_FIND_USERSTATUS)
        );

        //유저의 태그는 없는것으로 설정
        Users modifiedUser = user.toBuilder()
                .userStatusTagEntity(null)
                .build();

        userRepository.save(modifiedUser);

        log.info("userStatusTag = {}", findUserStatusTag.getUserStatusTagId());

        //유저태그모두 삭제
        userStatusTagRepository.delete(findUserStatusTag);
    }

    @Transactional
    public UserStatusTag update(@Valid ModifyUser request, Users user) {
        //내 상태 태그 생성
        List<MyStatusTags> myStatus = request.getMyStatus().stream()
                .map(status -> MyStatusTags.builder()
                        .myStatusTag(status)
                        .build())
                .toList();

        //내직업 엔티티 생성
        MyCareerTags myCareerTags = MyCareerTags.builder()
                .myCareerTagsEnum(request.getPersonalHistory())
                .build();

        CorporateFormEnum corporateFormEnum = request.getCorporateFormEnum() == null ? null : request.getCorporateFormEnum();

        UserStatusTag userStatusTagEntity = UserStatusTag.builder()
                .corporateFormEnum(corporateFormEnum)
                .myCareerTags(myCareerTags)
                .myStatus(myStatus) // 새로운 ArrayList로 변경
                .usersEntity(user)
                .build();

        userStatusTagRepository.save(userStatusTagEntity);


        myStatus.forEach(item -> {
            item = MyStatusTags.builder()
                    .myStatusTag(item.getMyStatusTag())
                    .userStatusTag(item.getUserStatusTag())
                    .build(); // toBuilder() 사용
            // 태그 엔티티 저장
            myStatusTagsEntityRepository.save(item); // 적절한 repository 사용
        });


        myCareerTagsEntityRepository.save(myCareerTags);

        return userStatusTagEntity;
    }


    public List<String> find(UserStatusTag userStatusTag) {
        List<String> myStatusTagsList = new ArrayList<>();

        userStatusTag.getMyStatus().forEach(
                myStatus -> myStatusTagsList.add(myStatus.getMyStatusTag().getDescription())

        );
        return myStatusTagsList;
    }

}