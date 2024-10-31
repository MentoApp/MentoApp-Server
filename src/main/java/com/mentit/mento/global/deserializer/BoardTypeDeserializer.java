package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.board.constant.BoardKeywordForCreating;
import com.mentit.mento.domain.board.constant.BoardType;

import java.io.IOException;

public class BoardTypeDeserializer extends JsonDeserializer<BoardType> {
    @Override
    public BoardType deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return BoardType.fromKoreanValue(name);
    }
}
