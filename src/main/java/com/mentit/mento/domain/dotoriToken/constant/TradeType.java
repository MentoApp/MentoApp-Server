package com.mentit.mento.domain.dotoriToken.constant;

import lombok.Getter;

@Getter
public enum TradeType {
    GIVE_BY_USER("사용"), TAKE_BY_USER("선물"), EARN("적립");

    private final String tradeType;

    TradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
