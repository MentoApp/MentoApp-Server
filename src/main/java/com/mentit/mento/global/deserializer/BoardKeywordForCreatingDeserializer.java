package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.board.constant.BoardKeywordForCreating;

import java.io.IOException;

public class BoardKeywordForCreatingDeserializer extends JsonDeserializer<BoardKeywordForCreating> {
    @Override
    public BoardKeywordForCreating deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return BoardKeywordForCreating.fromKoreanValue(name);
    }
}
