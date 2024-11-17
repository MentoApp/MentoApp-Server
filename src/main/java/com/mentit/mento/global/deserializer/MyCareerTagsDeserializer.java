package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.MyCareerTagsEnum;

import java.io.IOException;

public class MyCareerTagsDeserializer extends JsonDeserializer<MyCareerTagsEnum> {
    @Override
    public MyCareerTagsEnum deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return MyCareerTagsEnum.fromDescription(name);
    }
}
