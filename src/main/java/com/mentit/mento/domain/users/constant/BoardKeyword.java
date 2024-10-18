package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.BoardKeywordDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = BoardKeywordDeserializer.class)
public enum BoardKeyword {
    TREND("트렌드"),
    JOB_OR_JOB_CHANGE("취업/이직"),
    PRACTICAL_KNOWLEDGE("실무 노하우"),
    COMPANY_INFO("기업정보"),
    CAREER_GROWTH("커리어 성장"),
    LOOKING_FOR_MENTOR("멘토 찾아요"),
    INSIGHT("인사이트"),
    WORK_LIFE("회사 생활"),
    PRODUCT("프로덕트"),
    PROJECT("프로젝트"),
    INDUSTRY_TRENDS("업계 동향"),
    EDUCATION_REVIEW("교육 후기");

    private final String koreanValue;

    BoardKeyword(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public static BoardKeyword fromKoreanValue(String koreanValue) {
        for (BoardKeyword keyword : values()) {
            if (keyword.koreanValue.equals(koreanValue)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("잘못된 키워드: " + koreanValue);
    }
}
