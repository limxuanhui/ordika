package io.bluextech.ordika.services;
/* Created by limxuanhui on 24/12/23 */

import io.bluextech.ordika.models.Tale;
import io.bluextech.ordika.models.TaleMetadata;
import io.bluextech.ordika.repositories.TaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaleService {

    @Autowired
    private TaleRepository taleRepository;

    public Tale getTaleByTaleId(String taleId) {
        return taleRepository.getTaleByTaleId(taleId);
    }

    public List<TaleMetadata> getTalesMetadataPage(String cursor) {
        return taleRepository.getNextTalesMetadataPage(cursor);
    }

    public List<TaleMetadata> getTalesMetadataPageByUserId(String userId) {
        return taleRepository.getNextTalesMetadataPageByUserId(userId);
    }

    public Tale createTale(Tale tale) {
        return taleRepository.saveTale(tale);
    }

    public Tale updateTale(Tale tale) {
        return taleRepository.saveTale(tale);
    }

    public String deleteTaleByTaleId(String taleId) {
        return taleRepository.deleteTaleByTaleId(taleId);
    }

}