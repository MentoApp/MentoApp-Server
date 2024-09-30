package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.CooperateFormDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = CooperateFormDeserializer.class)
public enum CorporateForm {
    STARTUP("스타트업"),
    AGENCY("대행사"),
    MID_SIZED_FIRM("중소기업"),
    MAJOR_COMPANY("대기업"),
    IT_COMPANY("IT 회사"),
    PUBLIC_ENTERPRISE("공기업"),
    GLOBAL_COMPANY("글로벌 기업"),
    UNICORN_COMPANY("유니콘 기업");

    private final String koreanValue;

    CorporateForm(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public static CorporateForm fromKoreanValue(String koreanValue) {
        for (CorporateForm form : values()) {
            if (form.koreanValue.equals(koreanValue)) {
                return form;
            }
        }
        throw new IllegalArgumentException("잘못된 기업 형태: " + koreanValue);
    }
}
