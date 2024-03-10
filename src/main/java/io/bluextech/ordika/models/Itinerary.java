package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "itineraries")
public class Itinerary {
    @Id
    private UUID id;

    @Column(name = "creator_id")
    private String creatorId;
//    private List<Route> routes;

}
