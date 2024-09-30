package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.BoardKeyword;

import java.io.IOException;

public class BoardKeywordDeserializer extends JsonDeserializer<BoardKeyword> {
    @Override
    public BoardKeyword deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return BoardKeyword.fromKoreanValue(name);
    }
}
