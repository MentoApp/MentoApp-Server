package com.mentit.mento.domain.users.domain;

import com.mentit.mento.domain.users.constant.MyStatusTagsEnum;
import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class MyStatusTags {

    private Long myStatusTagId;
    private MyStatusTagsEnum myStatusTag; // 상태 태그
    private UserStatusTagEntity userStatusTag; // UserStatusTag 참조


}
