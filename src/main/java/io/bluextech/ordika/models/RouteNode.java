package io.bluextech.ordika.models;
/* Created by limxuanhui on 5/1/24 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteNode {

    private String id;
    private String placeId;
    private String name;
    private String address;
    private Coordinates coord;
    private String colour;
    private Integer order;

    public RouteNode(String id, String placeId, String name, String address, Coordinates coord, String colour, Integer order) {
        this.id = id;
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.coord = coord;
        this.colour = colour;
        this.order = order;
    }

    @Override
    public String toString() {
        return "RouteNode{" +
                "id='" + id + '\'' +
                ", placeId='" + placeId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", coord=" + coord +
                ", colour='" + colour + '\'' +
                ", order=" + order +
                '}';
    }

}