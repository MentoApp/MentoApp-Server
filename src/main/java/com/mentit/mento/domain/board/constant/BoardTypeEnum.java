package com.mentit.mento.domain.board.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.BoardTypeDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = BoardTypeDeserializer.class)
public enum BoardTypeEnum {
    DAILY_IT("IT 일상"), ARTICLE("아티클");

    private final String koreanValue;

    BoardTypeEnum(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public static String fromEnumValue(BoardTypeEnum authType) {
        for (BoardTypeEnum form : values()) {
            if (form.equals(authType)) {
                return form.koreanValue;
            }
        }
        throw new IllegalArgumentException("잘못된 게시판 종류: " + authType);
    }
    public static BoardTypeEnum fromKoreanValue(String koreanValue) {
        for (BoardTypeEnum keyword : values()) {
            if (keyword.koreanValue.equals(koreanValue)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("잘못된 게시판 종류: " + koreanValue);
    }

}
