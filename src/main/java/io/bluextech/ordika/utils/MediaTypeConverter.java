//package io.bluextech.ordika.utils;
///* Created by limxuanhui on 8/1/24 */
//
//import io.bluextech.ordika.enums.MediaType;
//
//import javax.persistence.AttributeConverter;
//import javax.persistence.Converter;
//import java.util.stream.Stream;
//
//@Converter(autoApply = true)
//public class MediaTypeConverter implements AttributeConverter<MediaType, String> {
//
//    @Override
//    public String convertToDatabaseColumn(MediaType mediaType) {
//        if (mediaType == null) {
//            return null;
//        }
//        return mediaType.getType();
//    }
//
//    @Override
//    public MediaType convertToEntityAttribute(String mediaType) {
//        if (mediaType == null) {
//            return null;
//        }
//        return Stream.of(MediaType.values())
//                .filter(type -> type.getType().equals(mediaType))
//                .findFirst()
//                .orElseThrow(IllegalArgumentException::new);
//    }
//
//}
