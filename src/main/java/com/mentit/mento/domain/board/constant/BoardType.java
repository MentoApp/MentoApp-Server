package com.mentit.mento.domain.board.constant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mentit.mento.global.deserializer.BoardTypeDeserializer;
import lombok.Getter;

@Getter
@JsonDeserialize(using = BoardTypeDeserializer.class)
public enum BoardType {
    DAILY_IT("IT 일상"), ARTICLE("아티클");

    private final String koreanValue;

    BoardType(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public static String fromEnumValue(BoardType authType) {
        for (BoardType form : values()) {
            if (form.equals(authType)) {
                return form.koreanValue;
            }
        }
        throw new IllegalArgumentException("잘못된 게시판 종류: " + authType);
    }
    public static BoardType fromKoreanValue(String koreanValue) {
        for (BoardType keyword : values()) {
            if (keyword.koreanValue.equals(koreanValue)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("잘못된 게시판 종류: " + koreanValue);
    }

}
