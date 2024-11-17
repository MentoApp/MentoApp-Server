package com.mentit.mento.domain.users.domain;

import com.mentit.mento.domain.users.constant.CorporateFormEnum;
import com.mentit.mento.domain.users.domain.entity.MyCareerTagsEntity;
import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class UserStatusTag {
    private Long userStatusTagId;
    private CorporateFormEnum corporateFormEnum; // 회사형태 (단일 선택)
    private List<MyStatusTagsEntity> myStatus = new ArrayList<>(); // 복수 선택 가능한 태그 카테고리
    private MyCareerTagsEntity myCareerTags; // 연차
    private UsersEntity usersEntity;


    public static UserStatusTagEntity from(UserStatusTag userStatusTag) {
        return UserStatusTagEntity.builder()
                .usersEntity(userStatusTag.getUsersEntity())
                .corporateFormEnum(userStatusTag.getCorporateFormEnum())
                .myStatus(userStatusTag.getMyStatus())
                .myCareerTags(userStatusTag.getMyCareerTags())
                .build();
    }

    public static UserStatusTag to(UserStatusTagEntity userStatusTagEntity) {
        return UserStatusTag.builder()
                .corporateFormEnum(userStatusTagEntity.getCorporateFormEnum())
                .myStatus(userStatusTagEntity.getMyStatus())
                .myCareerTags(userStatusTagEntity.getMyCareerTags())
                .build();
    }
}
