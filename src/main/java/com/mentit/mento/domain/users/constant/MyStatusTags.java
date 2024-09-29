package com.mentit.mento.domain.users.constant;

import lombok.Getter;

@Getter
public enum MyStatusTags {
    PROJECTOR("프로젝트중"),
    DESIGNER_OR_DEVELOPER("1인 디자이너 or 기획자 or 개발자"),
    INTERN("인턴 중"),
    JOB_SEEKER("취준생"),
    JOB_CONSIDERING("퇴사 고려 중"),
    GRADUATING("이직 준비 중"),
    BOOTCAMP_GRADUATE("부트캠프 수료"),
    BOOTCAMP_STUDENT("부트캠프 수강 중"),
    UNIVERSITY_STUDENT("대학생"),
    TEAM_LEADER("팀리더"),
    SOCIAL_BEGINNER("사회초년생"),
    FREELANCER("프리랜서");

    private final String description;

    MyStatusTags(String description) {
        this.description = description;
    }

}

