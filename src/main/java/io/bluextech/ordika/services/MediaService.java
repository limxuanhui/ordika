package io.bluextech.ordika.services;
/* Created by limxuanhui on 14/1/24 */

import io.bluextech.ordika.models.Media;
import io.bluextech.ordika.repositories.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MediaService {

    @Autowired
    private final MediaRepository mediaRepository;

//    @Autowired
//    private final MediaThumbnailService mediaThumbnailService;

    public Boolean saveMedia(Media media) {
        Media savedMedia = mediaRepository.save(media);
        return true;
    }

//    public Boolean saveMediaThumbnail(Media media) {
//        mediaThumbnailService.saveMediaThumbnail(media);
//        return true;
//    }

}
