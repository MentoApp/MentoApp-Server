package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.MyStatusTagsEnum;

import java.io.IOException;

public class MyStatusTagsDeserializer extends JsonDeserializer<MyStatusTagsEnum> {
    @Override
    public MyStatusTagsEnum deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return MyStatusTagsEnum.fromDescription(name);
    }
}
