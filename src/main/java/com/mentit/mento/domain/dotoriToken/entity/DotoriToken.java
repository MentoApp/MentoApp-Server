package com.mentit.mento.domain.dotoriToken.entity;

import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.global.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class DotoriToken extends BaseEntity {

    private Long dotoriTokenId;

    private int count;

    private Users usersEntity;

    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

}
