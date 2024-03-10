package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 24/12/23 */

import io.bluextech.ordika.models.Tale;
import io.bluextech.ordika.services.TaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tales")
public class TaleController {

    @Autowired
    private TaleService taleService;

    @GetMapping("/metadata")
    public void getTalesMetadata() {
        taleService.getTalesMetadataPage();
    }

    @GetMapping("/{id}")
    public Tale getTale(@PathVariable UUID id) {
        Tale result = taleService.getTale(id);
        System.out.println("Fetched tale: " + result);
        return result;
    }

    @PostMapping()
    public void createTale() {}

    @PutMapping()
    public void editTale() {}

    @DeleteMapping("/{id}")
    public void deleteTale() {}
}
