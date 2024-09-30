package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.BoardKeywordDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = BoardKeywordDeserializer.class)
public enum BoardKeyword {
    HUMAN_RELATIONS("인간관계"),
    LOOKING_FOR_MENTOR("멘토 찾아요"),
    SALARY_NEGOTIATION("연봉 협상"),
    RESUME("이력서"),
    PORTFOLIO_FEEDBACK("포폴 피드백"),
    BOOTCAMP_REVIEW("부트캠프 후기"),
    COMPANY_LIFE("회사 생활"),
    SELF_DEVELOPMENT("자기 계발"),
    CAREER_GROWTH("커리어 성장"),
    INSIGHTS("인사이트"),
    POLL("투표"),
    INTERVIEW_FEEDBACK("면접 후기"),
    PRACTICAL_TIPS("실무 노하우"),
    DAILY_LIFE("일상"),
    SHARE_THOUGHTS("생각 공유"),
    JOB_CHANGE("이직"),
    JOB_SEARCH_CONCERNS("취업 고민"),
    WORK_CONCERNS("업무 고민");

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
