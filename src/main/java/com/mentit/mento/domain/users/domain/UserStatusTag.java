package com.mentit.mento.domain.users.domain;

import com.mentit.mento.domain.users.constant.CorporateFormEnum;
import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class UserStatusTag {
    private Long userStatusTagId;
    private CorporateFormEnum corporateFormEnum; // 회사형태 (단일 선택)
    @Builder.Default
    private List<MyStatusTagsEntity> myStatus = new ArrayList<>(); // 복수 선택 가능한 태그 카테고리
    private MyCareerTags myCareerTags; // 연차
    private Users users;


}
