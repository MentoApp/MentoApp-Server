package com.mentit.mento.domain.dotoriToken.entity;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.dotoriToken.constant.TradeTypeEnum;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.global.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DotoriTokenUsageDetails extends BaseEntity {

    private Long dotoriTokenUsageDetailId;

    private TradeTypeEnum tradeTypeEnum;

    private int tradeAmount;

    private Users presenter;

    private Users receiver;

    private DotoriToken dotoriTokenEntity;

    private Board boardEntity;

    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

}
