package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "feed_items")
public class FeedItem {

    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "media_id")
    private Media media;

    @Column(name = "caption")
    private String caption;

    @Column(name = "feed_id")
    private UUID feedId;

}
