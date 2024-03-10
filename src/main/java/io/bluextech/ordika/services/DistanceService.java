package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/9/22 */

import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
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

    public DistanceMatrix getDistanceMatrix(List<Coordinates> coordinatesList) {
        List<LatLng> origins = coordinatesList.stream()
                .map(coordinates -> new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                .toList();
        List<LatLng> destinations = coordinatesList.stream()
                .map(coordinates -> new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                .toList();

        TravelMode mode = TravelMode.DRIVING;
        Unit unit = Unit.METRIC;
        DistanceMatrix distanceMatrix = null;
        DistanceMatrixApiRequest distanceMatrixApiRequest = new DistanceMatrixApiRequest(geoApiContext);
        try {
            distanceMatrix = distanceMatrixApiRequest
                    .origins(origins.toArray(LatLng[]::new))
                    .destinations(destinations.toArray(LatLng[]::new))
                    .mode(mode)
                    .units(unit)
                    .await();

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return distanceMatrix;
    }
}
