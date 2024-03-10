package io.bluextech.ordika.configs;
/* Created by limxuanhui on 29/11/23 */

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amazon.aws.s3")
public class AWSConfig {

    private String ACCESS_KEY;
    private String SECRET_KEY;

    public AWSConfig() {
        System.out.println("AWSConfig constructor called: " + ACCESS_KEY + " | " + SECRET_KEY);
    }

    public String getACCESS_KEY() {
        return ACCESS_KEY;
    }

    public void setACCESS_KEY(String ACCESS_KEY) {
        this.ACCESS_KEY = ACCESS_KEY;
    }

    public String getSECRET_KEY() {
        return SECRET_KEY;
    }

    public void setSECRET_KEY(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.AP_SOUTHEAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
