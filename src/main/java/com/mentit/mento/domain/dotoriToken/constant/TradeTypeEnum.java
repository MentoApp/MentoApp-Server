package com.mentit.mento.domain.dotoriToken.constant;

import lombok.Getter;

@Getter
public enum TradeTypeEnum {
    BOARD_GIVE_BY_USER("보내기"), BOARD_EARN("받기"), CHARGING("충전"), ENROLLMENT("가입"), BOARD_CREATE("게시물 작성");

    private final String tradeType;

    TradeTypeEnum(String tradeType) {
        this.tradeType = tradeType;
    }
}
