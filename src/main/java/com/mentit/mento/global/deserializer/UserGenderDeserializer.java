package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.UserGender;

import java.io.IOException;

public class UserGenderDeserializer extends JsonDeserializer<UserGender> {
    @Override
    public UserGender deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return UserGender.fromGender(name);
    }
}
