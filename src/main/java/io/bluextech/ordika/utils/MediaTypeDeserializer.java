package io.bluextech.ordika.utils;
/* Created by limxuanhui on 7/1/24 */

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.bluextech.ordika.enums.MediaType;

import java.io.IOException;

public class MediaTypeDeserializer extends JsonDeserializer<MediaType> {

    @Override
    public MediaType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String type = p.getValueAsString();
        return MediaType.fromType(type);
    }

}
