//package io.bluextech.ordika.models;
///* Created by limxuanhui on 14/1/24 */
//
//import io.bluextech.ordika.enums.MediaType;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.Entity;
//import javax.persistence.Table;
//
//@NoArgsConstructor
//@Entity
//@Table(name = "medias_tbn")
//public class MediaThumbnail extends Media {
//
//    public MediaThumbnail(Media media) {
//        MediaType type = media.getType();
//        Integer height;
//        Integer width;
//        if (media.getHeight() < 200 || media.getWidth() < 200) {
//            height = media.getHeight();
//            width = media.getWidth();
//        } else {
//            height = Math.min(media.getHeight() / 2, 200);
//        }
//
//        String uri = "tbn-" + media.getUri();
//
//        setType(type);
//        setHeight(height);
//        setWidth(width);
//        setUri(uri);
//    }
//
//}
