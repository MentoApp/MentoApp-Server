package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.MyStatusTags;

import java.io.IOException;

public class MyStatusTagsDeserializer extends JsonDeserializer<MyStatusTags> {
    @Override
    public MyStatusTags deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return MyStatusTags.fromDescription(name);
    }
}
