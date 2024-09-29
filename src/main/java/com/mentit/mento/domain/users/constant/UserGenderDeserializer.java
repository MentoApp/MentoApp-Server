package com.mentit.mento.domain.users.constant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class UserGenderDeserializer extends JsonDeserializer<UserGender> {
    @Override
    public UserGender deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return UserGender.nameOf(name);
    }
}
