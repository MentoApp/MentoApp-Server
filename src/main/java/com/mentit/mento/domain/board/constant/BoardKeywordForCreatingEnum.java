package com.mentit.mento.domain.board.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.BoardKeywordForCreatingDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = BoardKeywordForCreatingDeserializer.class)
public enum BoardKeywordForCreatingEnum {
    PLAN_OR_PM("기획/PM"),
    UI_UX_DESIGN("UI/UX 디자인"),
    BRAND_DESIGN("브랜드 디자인"),
    BACKEND("백엔드 개발"),
    FRONTEND("프론트엔드 개발"),
    AI_DEVELOPER("AI/데이터"),
    TREND("트렌드"),
    JOB_OR_JOB_CHANGE("취업/이직"),
    PRACTICAL_KNOWLEDGE("실무 노하우"),
    COMPANY_INFO("기업 정보"),
    CAREER_GROWTH("커리어 성장"),
    LOOKING_FOR_MENTOR("멘토 찾아요"),
    INSIGHT("인사이트"),
    WORK_LIFE("회사 생활"),
    PRODUCT("프로덕트"),
    PROJECT("프로젝트"),
    INDUSTRY_TRENDS("업계 동향"),
    EDUCATION_REVIEW("교육 후기");

    private final String koreanValue;

    BoardKeywordForCreatingEnum(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public static BoardKeywordForCreatingEnum fromKoreanValue(String koreanValue) {
        for (BoardKeywordForCreatingEnum keyword : values()) {
            if (keyword.koreanValue.equals(koreanValue)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("잘못된 키워드: " + koreanValue);
    }
}
