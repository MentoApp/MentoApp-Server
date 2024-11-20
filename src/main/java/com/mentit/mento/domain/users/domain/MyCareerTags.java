package com.mentit.mento.domain.users.domain;

import com.mentit.mento.domain.users.constant.MyCareerTagsEnum;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class MyCareerTags {

    private Long myCareerTagsId;
    private MyCareerTagsEnum myCareerTagsEnum; // 상태 태그
    private UserStatusTagEntity userStatusTagEntity; // UserStatusTag 참조


}
