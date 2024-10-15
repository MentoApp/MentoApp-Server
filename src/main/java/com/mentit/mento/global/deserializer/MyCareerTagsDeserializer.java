package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.MyCareerTags;

import java.io.IOException;

public class MyCareerTagsDeserializer extends JsonDeserializer<MyCareerTags> {
    @Override
    public MyCareerTags deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return MyCareerTags.fromDescription(name);
    }
}
