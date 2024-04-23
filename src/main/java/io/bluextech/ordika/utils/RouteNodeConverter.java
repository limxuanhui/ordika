package io.bluextech.ordika.utils;
/* Created by limxuanhui on 10/3/24 */

import io.bluextech.ordika.models.Coordinates;
import io.bluextech.ordika.models.RouteNode;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteNodeConverter implements AttributeConverter<List<RouteNode>> {

    @Override
    public AttributeValue transformFrom(List<RouteNode> routeNodes) {
        List<AttributeValue> routeNodesAttrValsList = new ArrayList<>();
        for (RouteNode routeNode : routeNodes) {
            Coordinates coordinates = routeNode.getCoord();
            AttributeValue attributeValue = AttributeValue.fromM(
                    Map.of(
                            "id", AttributeValue.fromS(routeNode.getId()),
                            "placeId", AttributeValue.fromS(routeNode.getPlaceId()),
                            "name", AttributeValue.fromS(routeNode.getName()),
                            "address", AttributeValue.fromS(routeNode.getAddress()),
                            "coord", AttributeValue.fromM(
                                    Map.of(
                                            "latitude", AttributeValue.fromN(coordinates.getLatitude().toString()),
                                            "longitude", AttributeValue.fromN(coordinates.getLongitude().toString())
                                    )),
                            "colour", AttributeValue.fromS(routeNode.getColour()),
                            "order", AttributeValue.fromN(routeNode.getOrder().toString())
                    )
            );
            routeNodesAttrValsList.add(attributeValue);
        }

        return AttributeValue.fromL(routeNodesAttrValsList);
    }

    @Override
    public List<RouteNode> transformTo(AttributeValue attributeValue) {
        List<AttributeValue> attributeValueList = attributeValue.l();
        List<RouteNode> routeNodeList = new ArrayList<>();
        for (AttributeValue attrVal : attributeValueList) {
            Map<String, AttributeValue> map = attrVal.m();
            routeNodeList.add(
                    new RouteNode(
                            map.get("id").s(),
                            map.get("placeId").s(),
                            map.get("name").s(),
                            map.get("address").s(),
                            new Coordinates(
                                    Float.valueOf(map.get("coord").m().get("latitude").n()),
                                    Float.valueOf(map.get("coord").m().get("longitude").n())
                            ),
                            map.get("colour").s(),
                            Integer.valueOf(map.get("order").n())
                    )
            );
        }

        return routeNodeList;
    }

    @Override
    public EnhancedType<List<RouteNode>> type() {
        return EnhancedType.listOf(RouteNode.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }

}