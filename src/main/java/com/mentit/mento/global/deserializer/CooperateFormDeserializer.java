package com.mentit.mento.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mentit.mento.domain.users.constant.CorporateForm;

import java.io.IOException;

public class CooperateFormDeserializer extends JsonDeserializer<CorporateForm> {
    @Override
    public CorporateForm deserialize(JsonParser p, DeserializationContext text) throws IOException {
        String name = p.getText();
        return CorporateForm.fromKoreanValue(name);
    }
}
