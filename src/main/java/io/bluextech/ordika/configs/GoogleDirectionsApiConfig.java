package io.bluextech.ordika.configs;
/* Created by limxuanhui on 24/1/23 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("google.api.directions")
public class GoogleDirectionsApiConfig {

    private String API_KEY;
    private String UNITS;
    private String MODE;
    private String TRANSIT_MODE;
    private String OUTPUT_FORMAT;
    private String BASE_URL;

    public GoogleDirectionsApiConfig() {
        System.out.println("Google directions api config constructor called");
        System.out.println("GOOGLE API KEY: " + API_KEY);
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getUNITS() {
        return UNITS;
    }

    public void setUNITS(String UNITS) {
        this.UNITS = UNITS;
    }

    public String getMODE() {
        return MODE;
    }

    public void setMODE(String MODE) {
        this.MODE = MODE;
    }

    public String getTRANSIT_MODE() {
        return TRANSIT_MODE;
    }

    public void setTRANSIT_MODE(String TRANSIT_MODE) {
        this.TRANSIT_MODE = TRANSIT_MODE;
    }

    public String getOUTPUT_FORMAT() {
        return OUTPUT_FORMAT;
    }

    public void setOUTPUT_FORMAT(String OUTPUT_FORMAT) {
        this.OUTPUT_FORMAT = OUTPUT_FORMAT;
    }

    public String getBASE_URL() {
        return BASE_URL;
    }

    public void setBASE_URL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }
}
