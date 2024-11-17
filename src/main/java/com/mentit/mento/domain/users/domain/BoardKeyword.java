package com.mentit.mento.domain.users.domain;

import com.mentit.mento.domain.users.constant.BoardKeywordEnum;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardKeyword {
    private Long boardKeywordId;
    private BoardKeywordEnum boardKeyword;
    private UsersEntity usersEntity;
}
