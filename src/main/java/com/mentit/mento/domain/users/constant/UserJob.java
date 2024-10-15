package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.UserJobDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = UserJobDeserializer.class)
public enum UserJob {
    PLAN_OR_PM("기획/PM"),
    UI_UX_DESIGN("UI/UX 디자인"),
    BRAND_DESIGN("브랜드 디자인"),
    BACKEND("백엔드 개발"),
    FRONTEND("프론트엔드 개발"),
    AI_DEVELOPER("AI 개발");

    private final String koreanValue;

    UserJob(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public static UserJob fromKoreanValue(String koreanValue) {
        for (UserJob job : values()) {
            if (job.koreanValue.equals(koreanValue)) {
                return job;
            }
        }
        throw new IllegalArgumentException("잘못된 직업: " + koreanValue);
    }
}
