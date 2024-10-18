package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.BaseTagDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = BaseTagDeserializer.class)
public enum BaseTag {
    MAJOR("전공자"),
    NON_MAJOR("비전공자"),
    JOB_CHANGER("직무전환자"),
    BOOTCAMP_GRADUATE("부트캠프 수료자");

    private final String description;

    BaseTag(String description) {
        this.description = description;
    }

    public static BaseTag fromDescription(String description) {
        for (BaseTag tag : values()) {
            if (tag.getDescription().equals(description)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("잘못된 설명: " + description);
    }
}

