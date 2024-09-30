package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.UserJobDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = UserJobDeserializer.class)
public enum UserJob {
    SERVICE_PLAN("서비스 기획"),
    BRAND_MARKETING("브랜드 마케팅"),
    GROSS_MARKETING("그로스 마케팅"),
    UI_UX_DESIGN("UI/UX 디자인"),
    BRAND_DESIGN("브랜드 디자인"),
    MARKETING_WEB_DESIGN("마케팅 웹 디자인"),
    BACKEND("백엔드 개발"),
    FRONTEND("프론트엔드 개발"),
    WEB_DEVELOPER("웹 개발자"),
    APP_DEVELOPER("앱 개발자"),
    AI_DEVELOPER("AI 개발자"),
    PROMPTER("프롬프터"),
    DATA_MANAGEMENT_DEVELOPER("데이터 관리 개발자");

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
