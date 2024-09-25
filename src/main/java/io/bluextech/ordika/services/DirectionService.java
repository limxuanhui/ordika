package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/9/22 */

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;
import io.bluextech.ordika.configs.GoogleDirectionsApiConfig;
import io.bluextech.ordika.models.Coordinates;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class DirectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectionService.class);
    private final GeoApiContext geoApiContext;
    private final GoogleDirectionsApiConfig googleDirectionsApiConfig;

    public DirectionsResult getDirections(Coordinates origin, Coordinates destination) {
        DirectionsResult directionsResult = null;
        LatLng start = new LatLng(origin.getLatitude(), origin.getLongitude());
        LatLng end = new LatLng(destination.getLatitude(), destination.getLongitude());
        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(geoApiContext);
        try {
            directionsResult = directionsApiRequest
                    .origin(start)
                    .destination(end)
                    .mode(TravelMode.valueOf(googleDirectionsApiConfig.getMODE()))
//                    .mode(TravelMode.DRIVING)
//                    .transitMode(TransitMode.TRAIN)
                    .transitMode(TransitMode.valueOf(googleDirectionsApiConfig.getTRANSIT_MODE()))
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            LOGGER.error("Error occurred when getting directions: " + e.getMessage());
        }

        return directionsResult;
    }

}
