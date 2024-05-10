package io.bluextech.ordika.services;
/* Created by limxuanhui on 24/12/23 */

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bluextech.ordika.dto.UpdateTaleRequestBody;
import io.bluextech.ordika.models.BaseMetadata;
import io.bluextech.ordika.models.PagedResult;
import io.bluextech.ordika.models.Tale;
import io.bluextech.ordika.models.TaleMetadata;
import io.bluextech.ordika.repositories.TaleRepository;
import io.bluextech.ordika.utils.DynamoDbAttributeValueConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

@Service
public class TaleService {

    @Autowired
    private TaleRepository taleRepository;

    public Tale getTaleByTaleId(String taleId) {
        return taleRepository.getTaleByTaleId(taleId);
    }

    public PagedResult<TaleMetadata> getNextTalesMetadataPage(String base64Key) throws JsonProcessingException {
        Map<String, AttributeValue> exclusiveStartKey = null;
        if (base64Key != null) {
            exclusiveStartKey = DynamoDbAttributeValueConverter.decodeKeyFromBase64String(base64Key);
        }
        Page<TaleMetadata> taleMetadataPage = taleRepository.getNextTalesMetadataPage(exclusiveStartKey);
        String lastEvaluatedKey = null;
        if (taleMetadataPage.lastEvaluatedKey() != null) {
            lastEvaluatedKey = DynamoDbAttributeValueConverter.encodeKeyToBase64String(taleMetadataPage.lastEvaluatedKey());
        }
        return new PagedResult<>(taleMetadataPage.items(), lastEvaluatedKey);
    }

    public PagedResult<TaleMetadata> getNextTalesMetadataPageByUserId(String userId) throws JsonProcessingException {
        Page<TaleMetadata> taleMetadataPage = taleRepository.getNextTalesMetadataPageByUserId(userId);
        String lastEvaluatedKey = null;
        if (taleMetadataPage.lastEvaluatedKey() != null) {
            lastEvaluatedKey = DynamoDbAttributeValueConverter.encodeKeyToBase64String(taleMetadataPage.lastEvaluatedKey());
        }
        return new PagedResult<>(taleMetadataPage.items(), lastEvaluatedKey);
    }

    public List<TaleMetadata> getAllTalesMetadataByUserId(String userId) {
        return taleRepository.getAllTalesMetadataByUserId(userId);
    }

    public List<TaleMetadata> getAllUserTalesMetadataByUserId(String userId) {
        return taleRepository.getAllUserTalesMetadataByUserId(userId);
    }

    public List<BaseMetadata> getAllItineraryMetadataByTaleIdList(List<String> taleIds) {
        return taleRepository.getAllItineraryMetadataByTaleIdList(taleIds);
    }

    public List<TaleMetadata> activateAllTalesByUserId(String userId) {
        List<TaleMetadata> allTalesMetadata = getAllTalesMetadataByUserId(userId);
        List<TaleMetadata> allUserTalesMetadata = getAllUserTalesMetadataByUserId(userId);
        List<BaseMetadata> allItineraryMetadata = getAllItineraryMetadataByTaleIdList(allTalesMetadata.stream()
                .map(BaseMetadata::getId)
                .toList());
        allItineraryMetadata.forEach(itineraryMetadata -> {
            itineraryMetadata.getCreator().setIsDeactivated(false);
            itineraryMetadata.setId(null);
        });
        taleRepository.batchUpdateItineraryMetadata(allItineraryMetadata);
        allTalesMetadata.addAll(allUserTalesMetadata);
        allTalesMetadata.forEach(taleMetadata -> {
            taleMetadata.getCreator().setIsDeactivated(false);
            taleMetadata.setId(null);
            taleMetadata.setCover(null);
            taleMetadata.setThumbnail(null);
            taleMetadata.setTitle(null);
            taleMetadata.setGSI1PK(null);
            taleMetadata.setGSI1SK(null);
        });

        return taleRepository.batchUpdateTaleMetadata(allTalesMetadata);
    }

    public List<TaleMetadata> deactivateAllTalesByUserId(String userId) {
        List<TaleMetadata> allTalesMetadata = getAllTalesMetadataByUserId(userId);
        List<TaleMetadata> allUserTalesMetadata = getAllUserTalesMetadataByUserId(userId);
        List<BaseMetadata> allItineraryMetadata = getAllItineraryMetadataByTaleIdList(allTalesMetadata.stream()
                .map(BaseMetadata::getId)
                .toList());
        allItineraryMetadata.forEach(itineraryMetadata -> {
            itineraryMetadata.getCreator().setIsDeactivated(true);
            itineraryMetadata.setId(null);
        });
        taleRepository.batchUpdateItineraryMetadata(allItineraryMetadata);
        allTalesMetadata.addAll(allUserTalesMetadata);
        allTalesMetadata.forEach(taleMetadata -> {
            taleMetadata.getCreator().setIsDeactivated(true);
            taleMetadata.setId(null);
            taleMetadata.setCover(null);
            taleMetadata.setThumbnail(null);
            taleMetadata.setTitle(null);
            taleMetadata.setGSI1PK(null);
            taleMetadata.setGSI1SK(null);
        });

        return taleRepository.batchUpdateTaleMetadata(allTalesMetadata);
    }

    public Tale createTale(Tale tale) {
        return taleRepository.saveTale(tale);
    }

    public Tale updateTale(UpdateTaleRequestBody body) {
        return taleRepository.updateTale(body);
    }

    public String deleteTaleByTaleId(String taleId) {
        return taleRepository.deleteTaleByTaleId(taleId);
    }

}