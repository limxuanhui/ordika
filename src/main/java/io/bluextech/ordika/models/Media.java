package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import io.bluextech.ordika.enums.MediaType;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@Table(name = "medias")
public class Media {

    @Id
    private UUID id;

    @Column(name = "type")
    private MediaType type;

    @Column(name = "uri")
    private String uri;

    @Column(name = "height")
    private Integer height;

    @Column(name = "width")
    private Integer width;

}
