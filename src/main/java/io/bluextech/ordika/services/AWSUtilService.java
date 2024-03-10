//package io.bluextech.ordika.services;
///* Created by limxuanhui on 30/11/23 */
//
//import com.amazonaws.services.s3.AmazonS3;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
//
//import java.net.URL;
//import java.time.Duration;
//
//@Service
//public class AWSUtilService {
//
//    @Autowired
//    private AmazonS3 s3Client;
//
//    /**
//     * Create a presigned URL for uploading media files to s3
//     * @return
//     */
//    public URL getSecureS3UrlForObjectUpload() {
//        PutObjectRequest objectRequest = PutObjectRequest.builder()
//                .bucket("sundayappmedia")
//                .key("")
//                .contentType("")
//                .build();
//
//        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofSeconds(60))
//                .putObjectRequest(objectRequest)
//                .build();
//
//
//    }
//}
