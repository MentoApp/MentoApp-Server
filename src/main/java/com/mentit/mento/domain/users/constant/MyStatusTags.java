package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.MyStatusTagsDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = MyStatusTagsDeserializer.class)
public enum MyStatusTags {
    PROFESSIONAL_JOB_SEEKER("프로이직러"),
    UNIVERSITY_STUDENT("대학생"),
    CAREER_SWITCHER("직무 전환"),
    JOB_SEEKER("구직중"),
    ENTREPRENEUR("창업가"),
    TEAM_LEADER("팀리더"),
    NON_CS_BACKGROUND("비전공자 출신"),
    FREELANCER("프리랜서"),
    LECTURE_ENTHUSIAST("강의마니아"),
    MULTI_JOB_WORKER("N잡러"),
    CERTIFICATE_COLLECTOR("자격증 수집가"),
    STUDY_ADDICT("스터디 중독");

    private final String description;

    MyStatusTags(String description) {
        this.description = description;
    }

    public static MyStatusTags fromDescription(String description) {
        for (MyStatusTags status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("잘못된 상태 태그: " + description);
    }
}
