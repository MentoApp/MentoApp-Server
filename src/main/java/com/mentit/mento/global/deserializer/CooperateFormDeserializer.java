package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.CorporateFormEnum;

import java.io.IOException;

public class CooperateFormDeserializer extends JsonDeserializer<CorporateFormEnum> {
    @Override
    public CorporateFormEnum deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return CorporateFormEnum.fromKoreanValue(name);
    }
}
