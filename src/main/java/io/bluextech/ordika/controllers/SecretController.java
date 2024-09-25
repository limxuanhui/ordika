package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/7/24 */

import io.bluextech.ordika.services.SecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/secrets")
public class SecretController {

    private final SecretService secretService;

    @GetMapping("/places")
    public String getGooglePlacesApiKey() {
        return secretService.getGooglePlacesApiKey();
    }

}
