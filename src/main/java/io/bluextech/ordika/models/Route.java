package io.bluextech.ordika.models;
/* Created by limxuanhui on 3/1/24 */

import io.bluextech.ordika.utils.RouteNodeConverter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

import java.util.List;

@Setter
@DynamoDbBean
public class Route extends BaseDynamoDbItem implements Comparable<Route> {

    private String id;
    private String name;
    private List<RouteNode> routeNodes;
    private String encodedPolyline;
    private Integer order;

    public Route() {}

    public Route(String id, String name, List<RouteNode> routeNodes, String encodedPolyline, Integer order) {
        this.id = id;
        this.name = name;
        this.routeNodes = routeNodes;
        this.encodedPolyline = encodedPolyline;
        this.order = order;
    }

    @DynamoDbAttribute(value = "id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute(value = "name")
    public String getName() {
        return name;
    }

    @DynamoDbConvertedBy(RouteNodeConverter.class)
    @DynamoDbAttribute(value = "routeNodes")
    public List<RouteNode> getRouteNodes() {
        return routeNodes;
    }

    @DynamoDbAttribute(value = "encodedPolyline")
    public String getEncodedPolyline() {
        return encodedPolyline;
    }

    @DynamoDbAttribute(value = "order")
    public Integer getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", routeNodes=" + routeNodes +
                ", encodedPolyline='" + encodedPolyline + '\'' +
                '}';
    }

    @Override
    public int compareTo(Route o) {
        return this.order.compareTo(o.getOrder());
    }

}