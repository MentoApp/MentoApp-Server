package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.MyCareerTagsDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = MyCareerTagsDeserializer.class)
public enum MyCareerTags {
    NO_EXPERIENCE("경력없음"),
    ENTRY_LEVEL("신입~2년"),
    MID_LEVEL("3~5년"),
    SENIOR_LEVEL("6~9년"),
    EXPERT_LEVEL("10년 이상");

    private final String description;

    MyCareerTags(String description) {
        this.description = description;
    }

    public static MyCareerTags fromDescription(String description) {
        for (MyCareerTags career : values()) {
            if (career.getDescription().equals(description)) {
                return career;
            }
        }
        throw new IllegalArgumentException("잘못된 경력 태그: " + description);
    }
}
