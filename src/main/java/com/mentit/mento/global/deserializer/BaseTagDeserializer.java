package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.BaseTag;
import com.mentit.mento.domain.users.constant.BoardKeyword;

import java.io.IOException;

public class BaseTagDeserializer extends JsonDeserializer<BaseTag> {
    @Override
    public BaseTag deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return BaseTag.fromDescription(name);
    }
}
