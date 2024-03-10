package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "feeds")
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "tale_id")
    private UUID taleId;

    private transient List<FeedItem> items;

    public Feed(User creator) {
        this.creator = creator;
    }

}
