package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.CurrentJobStatusDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = CurrentJobStatusDeserializer.class)
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

    public static CurrentJobStatus fromDescription(String description) {
        for (CurrentJobStatus status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("잘못된 직업 상태: " + description);
    }
}
