//package io.bluextech.ordika.controllers;
///* Created by limxuanhui on 29/11/23 */
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.Bucket;
//import io.bluextech.ordika.annotations.JsonArg;
//import io.bluextech.ordika.models.Asset;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.File;
//import java.util.List;
//
//@RestController
//@RequestMapping("/itinerary")
//public class ItineraryPostController {
//
//    @Autowired
//    private AmazonS3 s3Client;
//
//    @PostMapping("/post")
//    public String submitPost(@JsonArg("coverMedia") Asset coverMedia,
//                             @JsonArg("title") String title,
//                             @JsonArg("storyData") List<Object> storyData) {
//        System.out.println("COVER_MEDIA: " + coverMedia);
//        System.out.println("TITLE: " + title);
//        System.out.println("STORY_DATA: " + storyData);
//        List<Bucket> buckets = s3Client.listBuckets();
//        System.out.println("Buckets: ");
//        for (Bucket bucket : buckets) System.out.println(bucket.getName());
//
//        s3Client.putObject(buckets.get(0).getName(), "photo1", new File(coverMedia.getUri()));
//        return "YEAH";
//    }
//}
