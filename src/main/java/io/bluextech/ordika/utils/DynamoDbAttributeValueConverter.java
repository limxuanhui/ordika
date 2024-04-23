package io.bluextech.ordika.utils;
/* Created by limxuanhui on 12/3/24 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference:
 * https://github.com/aws/aws-sdk-java-v2/issues/3224
 */
public class DynamoDbAttributeValueConverter {

    public static String encodeKeyToBase64String(Map<String, AttributeValue> key) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        key.forEach((k, v) -> map.put(k, v.s()));
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);
        return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public static Map<String, AttributeValue> decodeKeyFromBase64String(String base64Key) throws JsonProcessingException {
        Map<String, AttributeValue> key = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        String json = new String(Base64.getUrlDecoder().decode(base64Key), StandardCharsets.UTF_8);
        Map<String, String> parsedJsonMap = objectMapper.readValue(json, Map.class);
        parsedJsonMap.forEach((k, v) -> {
            key.put(k, AttributeValue.fromS(v));
        });
        return key;
    }

}
