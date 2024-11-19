package com.mentit.mento.domain.board.domain;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreatingEnum;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.global.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardKeywordForCreating extends BaseEntity {

    private Long boardKeywordId;

    private BoardKeywordForCreatingEnum boardKeyword;

    private Board boardEntity;

}
