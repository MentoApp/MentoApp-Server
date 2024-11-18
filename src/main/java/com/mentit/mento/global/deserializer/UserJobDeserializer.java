package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.UserJobEnum;

import java.io.IOException;

public class UserJobDeserializer extends JsonDeserializer<UserJobEnum> {
    @Override
    public UserJobEnum deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return UserJobEnum.fromKoreanValue(name);
    }
}
