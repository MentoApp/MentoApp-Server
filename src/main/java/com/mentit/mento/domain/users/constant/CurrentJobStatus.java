package com.mentit.mento.domain.users.constant;

import lombok.Getter;

@Getter
public enum CurrentJobStatus {
    ECOMMERCE("이커머스"),
    FINANCE("금융"),
    MOBILITY("모빌리티"),
    HEALTHCARE("헬스케어"),
    B2C("B2C"),
    B2B("B2B"),
    O2O("O2O"),
    AI_BIGDATA("AI/빅데이터"),
    CLOUD("클라우드"),
    GAMES("게임");

    private final String description;

    CurrentJobStatus(String description) {
        this.description = description;
    }

}

