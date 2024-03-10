package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "tales")
public class Tale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToOne
    @JoinColumn(name = "cover_id")
    private Media cover;

    @Column(name = "title")
    private String title;

    @OneToOne
    @JoinColumn(name = "itinerary_id")
    private Itinerary itinerary;

    private transient String story;

    private transient List<Feed> feeds;

}