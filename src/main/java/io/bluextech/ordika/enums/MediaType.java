package io.bluextech.ordika.enums;
/* Created by limxuanhui on 7/1/24 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bluextech.ordika.utils.MediaTypeDeserializer;

@JsonDeserialize(using = MediaTypeDeserializer.class)
public enum MediaType {
    IMAGE_PNG("image/png"),
    IMAGE_JPG("image/jpg"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_UNKNOWN("image/unknown"),
    VIDEO_MP4("video/mp4");

    private final String type;

    MediaType(String type) {
        this.type = type;
    }

    public static MediaType fromType(String type) {
        for (MediaType mediaType : MediaType.values()) {
            if (mediaType.getType().equalsIgnoreCase(type)) {
                return mediaType;
            }
        }
        throw new IllegalArgumentException("Unsupported media type: " + type);
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
