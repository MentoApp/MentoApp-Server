package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.board.constant.BoardTypeEnum;

import java.io.IOException;

public class BoardTypeDeserializer extends JsonDeserializer<BoardTypeEnum> {
    @Override
    public BoardTypeEnum deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return BoardTypeEnum.fromKoreanValue(name);
    }
}
