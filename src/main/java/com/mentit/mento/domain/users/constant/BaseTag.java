package com.mentit.mento.domain.users.constant;

import lombok.Getter;

@Getter
public enum BaseTag {
    MAJOR("전공자"),
    NON_MAJOR("비전공자"),
    JOB_CHANGER("직무전환자"),
    BOOTCAMP_GRADUATE("부트캠프수료자");

    private final String description;

    BaseTag(String description) {
        this.description = description;
    }

}

