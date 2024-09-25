package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/9/22 */

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import io.bluextech.ordika.models.Coordinates;
import io.bluextech.ordika.models.OrdikaDirections;
import io.bluextech.ordika.services.DirectionService;
import io.bluextech.ordika.services.DistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/directions")
public class DirectionController {

    private final DirectionService directionsService;
    private final DistanceService distanceService;

    private List<Integer> getUnvisited(Map<Integer, Boolean> visited) {
        return visited.entrySet()
                .stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Integer> getOrder(DistanceMatrix distanceMatrix) throws Exception {
        Map<Integer, Boolean> visited = new HashMap<>();
        List<Integer> orderedRoute = new ArrayList<>();

        for (int i = 0; i < distanceMatrix.rows.length; i++) {
            visited.put(i, i == 0);
        }
        int currentIdx = 0;
        orderedRoute.add(currentIdx);
        List<Integer> unvisited = getUnvisited(visited);

        // For every row in distance matrix
        while (!unvisited.isEmpty()) {
            if (currentIdx == -1) {
                return new ArrayList<>();
            }

            DistanceMatrixRow row = distanceMatrix.rows[currentIdx];

            // Find next nearest unvisited city
            long shortestTime = unvisited.stream()
                    .map(i -> {
                        if (row.elements[i].duration != null) {
                            return row.elements[i].duration.inSeconds;
                        }
                        return Long.MAX_VALUE;
                    })
                    .min(Long::compare)
                    .get();
            currentIdx = Arrays.stream(row.elements)
                    .map(el -> {
                        if (el.duration != null) {
                            return el.duration.inSeconds;
                        }
                        return 0;
                    })
                    .toList()
                    .indexOf(shortestTime);

            // Add index of next nearest unvisited city to ordered route
            orderedRoute.add(currentIdx);

            // Set next nearest unvisited city to visited in visited map
            visited.put(currentIdx, true);

            // Refresh unvisited cities
            unvisited = getUnvisited(visited);
        }
        return orderedRoute;
    }

    <T> List<T> getOrderedRoute(List<T> list, List<Integer> order) {
        List<T> newList = new ArrayList<>();

        if (order.size() == 0) {
            return newList;
        }

        for (int i = 0; i < list.size(); i++) {
            int nextId = order.get(i);
            newList.add(list.get(nextId));
        }
        return newList;
    }

    @PostMapping()
    public OrdikaDirections getDirections(@RequestBody List<Coordinates> coordinatesList) throws Exception {
        // TODO: Check if place_id exists - if yes we use placeId list instead of coordinates

        if (coordinatesList.size() < 2) {
            return null;
        }

        // Pass coordinates and get distance matrix
        DistanceMatrix distanceMatrix = distanceService.getDistanceMatrix(coordinatesList);

        // With distance matrix, use Nearest Neighbour Algorithm to find the Hamiltonian path:
        // Route nodes in sequence that leads to "shortest path" (shortest as in distance or time)
        List<Integer> order = getOrder(distanceMatrix);
        List<Coordinates> orderedRoute = getOrderedRoute(coordinatesList, order);

        // With "shortest path" sequence of route nodes, get direction instructions for each consecutive pair
        List<DirectionsResult> directionsResultList = new ArrayList<>();
        for (int i = 0; i < orderedRoute.size() - 1; i++) {
            DirectionsResult directionsResult = directionsService.getDirections(orderedRoute.get(i), orderedRoute.get(i+1));
            if (directionsResult != null) {
                directionsResultList.add(directionsResult);
            }
        }

        return new OrdikaDirections(directionsResultList, order);
    }
}
