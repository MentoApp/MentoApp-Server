package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = UserGenderDeserializer.class)
public enum UserGender {
    MALE("남성"), FEMALE("여성");

    private final String gender;
    UserGender(String gender) {
        this.gender = gender;
    }

    @JsonValue
    public String getGender() {
        return this.gender;
    }

    public static UserGender nameOf(String name) {
        System.out.println("nameOf가 수행되었습니다.");
        for (UserGender data : UserGender.values()) {
            if (data.getGender().equals(name)) {
                return data;
            }
        }
        throw new MemberException(ExceptionCode.INVALID_ENUM_PARAMETER);
    }
}
