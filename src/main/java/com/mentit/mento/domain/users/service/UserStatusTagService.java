package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.users.constant.CorporateForm;
import com.mentit.mento.domain.users.dto.request.ModifyUserRequest;
import com.mentit.mento.domain.users.dto.request.SignInUserRequest;
import com.mentit.mento.domain.users.entity.*;
import com.mentit.mento.domain.users.repository.*;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserStatusTagService {

    private final UserStatusTagRepository userStatusTagRepository;
    private final BaseEntityRepository baseEntityRepository;
    private final MyStatusTagsEntityRepository myStatusTagsEntityRepository;
    private final CurrentJobStatusEntityRepository currentJobStatusEntityRepository;
    private final MyCareerTagsEntityRepository myCareerTagsEntityRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserStatusTag createUserStatusTag(SignInUserRequest request, Users user) {
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

        MyCareerTagsEntity myCareerTagsEntities = MyCareerTagsEntity.builder()
                .myCareerTags(request.getPersonalHistory())
                .build();

        CorporateForm corporateForm = request.getCorporateForm();

        UserStatusTag userStatusTag = UserStatusTag.builder()
                .baseTags(new ArrayList<>(baseTagEntities))
                .corporateForm(corporateForm)
                .currentJobStatus(new ArrayList<>(currentJobStatusEntities))
                .myCareerTags(myCareerTagsEntities)
                .myStatus(new ArrayList<>(myStatusEntities))
                .users(user)
                .build();

        userStatusTagRepository.save(userStatusTag);

        // 각각의 엔티티에 UserStatusTag 연결
        baseTagEntities.forEach(baseTag -> baseEntityRepository.save(baseTag.toBuilder().userStatusTag(userStatusTag).build()));
        myStatusEntities.forEach(myStatus -> myStatusTagsEntityRepository.save(myStatus.toBuilder().userStatusTag(userStatusTag).build()));
        currentJobStatusEntities.forEach(jobStatus -> currentJobStatusEntityRepository.save(jobStatus.toBuilder().userStatusTag(userStatusTag).build()));

        myCareerTagsEntityRepository.save(myCareerTagsEntities);

        return userStatusTag;
    }

    public void deleteExistingUserStatusTag(Users user) {
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

    @Transactional
    public UserStatusTag createUserStatusTag(@Valid ModifyUserRequest request, Users user) {
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

        MyCareerTagsEntity myCareerTagsEntities = MyCareerTagsEntity.builder()
                .myCareerTags(request.getPersonalHistory())
                .build();

        CorporateForm corporateForm = request.getCorporateForm() == null ?null:request.getCorporateForm();

        UserStatusTag userStatusTag = UserStatusTag.builder()
                .baseTags(new ArrayList<>(baseTagEntities)) // 새로운 ArrayList로 변경
                .corporateForm(corporateForm)
                .currentJobStatus(new ArrayList<>(currentJobStatusEntities)) // 새로운 ArrayList로 변경
                .myCareerTags(myCareerTagsEntities)
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

        myCareerTagsEntityRepository.save(myCareerTagsEntities);

        return userStatusTag;
    }

    public List<String> getBaseTags(UserStatusTag userStatusTag) {
        List<String> baseTagList = new ArrayList<>();
        userStatusTag.getBaseTags().forEach(
                i -> baseTagList.add(i.getBaseTag().getDescription())
        );
        return baseTagList;
    }

    public  List<String> getCurrentJobStatuses(UserStatusTag userStatusTag) {
        List<String> currentJobStatusList = new ArrayList<>();

        userStatusTag.getCurrentJobStatus().forEach(
                currentJobStatus -> currentJobStatusList.add(currentJobStatus.getCurrentJobStatus().getDescription())
        );
        return currentJobStatusList;
    }

    public List<String> getMyStatusTags(UserStatusTag userStatusTag) {
        List<String> myStatusTagsList = new ArrayList<>();

        userStatusTag.getMyStatus().forEach(
                myStatus -> myStatusTagsList.add(myStatus.getMyStatusTag().getDescription())

        );
        return myStatusTagsList;
    }

}