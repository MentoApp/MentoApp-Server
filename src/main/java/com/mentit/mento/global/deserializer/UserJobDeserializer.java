package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.UserGender;
import com.mentit.mento.domain.users.constant.UserJob;

import java.io.IOException;

public class UserJobDeserializer extends JsonDeserializer<UserJob> {
    @Override
    public UserJob deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return UserJob.fromKoreanValue(name);
    }
}
