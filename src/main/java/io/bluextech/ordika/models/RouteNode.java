package io.bluextech.ordika.models;
/* Created by limxuanhui on 5/1/24 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteNode {

    private String placeId;
    private String name;
    private String address;
    private Coordinates coord;

    public RouteNode(String placeId, String name, String address, Coordinates coord) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.coord = coord;
    }

    @Override
    public String toString() {
        return "RouteNode{" +
                "placeId='" + placeId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", coord=" + coord +
                '}';
    }

}