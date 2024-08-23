package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/7/24 */

import io.bluextech.ordika.services.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secrets")
public class SecretController {

    @Autowired
    private SecretService secretService;

    @GetMapping("/places")
    public String getGooglePlacesApiKey() {
        return secretService.getGooglePlacesApiKey();
    }

}
