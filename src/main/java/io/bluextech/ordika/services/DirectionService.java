package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/9/22 */

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import io.bluextech.ordika.configs.GoogleDirectionsApiConfig;
import io.bluextech.ordika.models.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DirectionService {

    @Autowired
    private GeoApiContext geoApiContext;

    @Autowired
    private GoogleDirectionsApiConfig googleDirectionsApiConfig;

    public DirectionService() {
        System.out.println("DirectionsService constructor called");
    }

    public DirectionsResult getDirections(Coordinates origin, Coordinates destination) {
        DirectionsResult directionsResult = null;
        LatLng start = new LatLng(origin.getLatitude(), origin.getLongitude());
        LatLng end = new LatLng(destination.getLatitude(), destination.getLongitude());
        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(geoApiContext);
        try {
            directionsResult = directionsApiRequest
                    .origin(start)
                    .destination(end)
                    .mode(TravelMode.DRIVING)
//                    .transitMode(TransitMode.)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directionsResult;
    }
}
