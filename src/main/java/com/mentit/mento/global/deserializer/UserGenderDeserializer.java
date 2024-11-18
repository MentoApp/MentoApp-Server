package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.UserGenderEnum;

import java.io.IOException;

public class UserGenderDeserializer extends JsonDeserializer<UserGenderEnum> {
    @Override
    public UserGenderEnum deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return UserGenderEnum.fromGender(name);
    }
}
