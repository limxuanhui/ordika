package io.bluextech.ordika.models;
/* Created by limxuanhui on 20/11/22 */

import com.google.maps.model.DirectionsResult;

import java.util.List;
import java.util.Objects;

public class OrdikaDirections {
    private List<DirectionsResult> directionsResultList;

    private List<Integer> order;

    public OrdikaDirections(List<DirectionsResult> directionsResultList, List<Integer> order) {
        this.directionsResultList = directionsResultList;
        this.order = order;
    }

    public List<DirectionsResult> getDirectionsResultList() {
        return directionsResultList;
    }

    public void setDirectionsResultList(List<DirectionsResult> directionsResultList) {
        this.directionsResultList = directionsResultList;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public void setOrder(List<Integer> order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdikaDirections that = (OrdikaDirections) o;
        return Objects.equals(directionsResultList, that.directionsResultList) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directionsResultList, order);
    }

    @Override
    public String toString() {
        return "OrdikaDirections{" +
                "directionsResultList=" + directionsResultList +
                ", order=" + order +
                '}';
    }
}
