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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DirectionService {

    @Autowired
    private GeoApiContext geoApiContext;

    @Autowired
    private GoogleDirectionsApiConfig googleDirectionsApiConfig;

    private final String ORIGINS = "seoul%20train%20station";
    private final String DESTINATIONS = "hongik%20university";
//    private final String PARAMETERS;
//    private final String URL;

    public DirectionService() {
        System.out.println("DirectionsService constructor called");
//        PARAMETERS  = "?key=" + googleDirectionsApiConfig.getAPI_KEY()
//                + "&origins=" + ORIGINS
//                + "&destinations=" + DESTINATIONS
//                + "&mode=" + googleDirectionsApiConfig.getMODE()
//                + "&transit_mode=" + googleDirectionsApiConfig.getTRANSIT_MODE()
//                + "&units=" + googleDirectionsApiConfig.getUNITS();
//
//        URL = googleDirectionsApiConfig.getBASE_URL()
//                + googleDirectionsApiConfig.getOUTPUT_FORMAT()
//                + PARAMETERS;
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
                    .mode(TravelMode.TRANSIT)
                    .transitMode(TransitMode.RAIL)
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
