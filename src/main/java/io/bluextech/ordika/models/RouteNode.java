package io.bluextech.ordika.models;
/* Created by limxuanhui on 5/1/24 */

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "route_nodes")
public class RouteNode {
    @Id
    private String id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    @Transient
    private Boolean openNow;
}
