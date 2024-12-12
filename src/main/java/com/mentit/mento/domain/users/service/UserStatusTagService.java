package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.users.constant.CorporateFormEnum;
import com.mentit.mento.domain.users.domain.dto.request.ModifyUserRequest;
import com.mentit.mento.domain.users.domain.dto.request.SignInUserRequest;
import com.mentit.mento.domain.users.domain.entity.MyCareerTagsEntity;
import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
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
    public UserStatusTagEntity create(SignInUserRequest request, UsersEntity usersEntity) {
        // MyStatusTags 생성 및 null 체크
        List<MyStatusTagsEntity> myStatusTags = request.getMyStatus() != null
                ? request.getMyStatus().stream()
                .filter(Objects::nonNull)
                .map(status -> MyStatusTagsEntity.builder()
                        .myStatusTagEnum(status)
                        .build())
                .toList()
                : new ArrayList<>();

        // MyCareerTags 생성 및 null 체크
        MyCareerTagsEntity myCareerTags = request.getPersonalHistory() != null
                ? MyCareerTagsEntity.builder()
                .myCareerTagsEnum(request.getPersonalHistory())
                .build()
                : null;

        // UserStatusTag 생성
        UserStatusTagEntity userStatusTag = UserStatusTagEntity.builder()
                .corporateFormEnum(request.getCorporateFormEnum())
                .usersEntity(usersEntity)
                .build();

        // UserStatusTag 저장 (ID 생성)
        userStatusTag = userStatusTagRepository.save(userStatusTag);

        // 연관 관계 설정 후 저장
        if (!myStatusTags.isEmpty()) {
            UserStatusTagEntity finalUserStatusTag = userStatusTag;
            myStatusTags.forEach(tag -> tag.setUserStatusTagEntity(finalUserStatusTag)); // 참조 설정
            myStatusTagsEntityRepository.saveAll(myStatusTags);
        }

        if (myCareerTags != null) {
            myCareerTags.setUserStatusTagEntity(userStatusTag); // 참조 설정
            myCareerTagsEntityRepository.save(myCareerTags);
        }

        // UserStatusTag에 연관 데이터 설정
        userStatusTag = userStatusTag.toBuilder()
                .myStatus(myStatusTags)
                .myCareerTags(myCareerTags)
                .build();

        return userStatusTagRepository.save(userStatusTag); // 최종 저장
    }

    @Transactional
    public UsersEntity delete(UsersEntity usersEntity) {
        //유저의 모든 태그 조회
        UserStatusTagEntity userStatusTagEntity = userStatusTagRepository.findByUsers(usersEntity).orElseThrow(
                () -> new MemberException(ExceptionCode.CANT_FIND_USERSTATUS)
        );
        log.info("삭제할 statusTagId={}",userStatusTagEntity.getUserStatusTagId());

        //유저엔티티의의 태그는 없는것으로 설정
        UsersEntity modifiedUser = usersEntity.toBuilder()
                .userStatusTagEntity(null)
                .build();

        //유저태그엔티티모두 삭제
        userStatusTagRepository.delete(userStatusTagEntity);

        return userRepository.save(modifiedUser);
    }

    @Transactional
    public UserStatusTagEntity update(@Valid ModifyUserRequest request, UsersEntity usersEntity) {
        log.info("userEntity.userstatusTag 삭제 유무 : {}", usersEntity.getUserStatusTagEntity()==null);
        UserStatusTagEntity userStatusTagEntity = userStatusTagRepository.findByUsers(usersEntity).orElse(null);

        //내 상태 태그 생성
        List<MyStatusTagsEntity> myStatusTagsEntities = request.getMyStatus().stream()
                .map(status -> MyStatusTagsEntity.builder()
                        .myStatusTagEnum(status)
                        .build())
                .toList();


        //내직업 엔티티 생성
        MyCareerTagsEntity myCareerTagsEntity = MyCareerTagsEntity.builder()
                .myCareerTagsEnum(request.getPersonalHistory())
                .build();


        CorporateFormEnum corporateFormEnum = request.getCorporateFormEnum() == null ? null : request.getCorporateFormEnum();

        userStatusTagEntity = UserStatusTagEntity.builder()
                .corporateFormEnum(corporateFormEnum)
                .myCareerTags(myCareerTagsEntity)
                .myStatus(myStatusTagsEntities) // 새로운 ArrayList로 변경
                .usersEntity(usersEntity)
                .build();

        usersEntity.setUserStatusTagEntity(userStatusTagEntity);

        return userStatusTagRepository.save(userStatusTagEntity);
    }


    public List<String> find(UserStatusTagEntity userStatusTagEntity) {
        List<String> myStatusTagsList = new ArrayList<>();

        userStatusTagEntity.getMyStatus().forEach(
                myStatus -> myStatusTagsList.add(myStatus.getMyStatusTagEnum().getDescription())
        );
        return myStatusTagsList;
    }

}