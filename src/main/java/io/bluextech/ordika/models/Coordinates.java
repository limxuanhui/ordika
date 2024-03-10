package io.bluextech.ordika.models;
/* Created by limxuanhui on 10/9/22 */

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Coordinates {

    private Float latitude;
    private Float longitude;

    public Coordinates(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
