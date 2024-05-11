package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/9/22 */

import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import io.bluextech.ordika.configs.GoogleDirectionsApiConfig;
import io.bluextech.ordika.models.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DistanceService {

    // Set up GeoApiContext, refactor it to be singleton
    @Autowired
    private GeoApiContext geoApiContext;
    @Autowired
    private GoogleDirectionsApiConfig googleDirectionsApiConfig;

    public DistanceMatrix getDistanceMatrix(List<Coordinates> coordinatesList) {
        List<LatLng> origins = coordinatesList.stream()
                .map(coordinates -> new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                .toList();
        List<LatLng> destinations = coordinatesList.stream()
                .map(coordinates -> new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                .toList();

        TravelMode mode = TravelMode.valueOf(googleDirectionsApiConfig.getMODE()); //TravelMode.DRIVING;
        Unit unit = Unit.valueOf(googleDirectionsApiConfig.getUNITS());
        DistanceMatrix distanceMatrix = null;
        DistanceMatrixApiRequest distanceMatrixApiRequest = new DistanceMatrixApiRequest(geoApiContext);
        try {
            distanceMatrix = distanceMatrixApiRequest
                    .origins(origins.toArray(LatLng[]::new))
                    .destinations(destinations.toArray(LatLng[]::new))
                    .mode(mode)
                    .units(unit)
                    .await();

        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return distanceMatrix;
    }
}
