package io.bluextech.ordika.models;
/* Created by limxuanhui on 13/7/23 */

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "handle")
    private String handle;

    @Column(name = "email")
    private String email;

    @OneToOne
    @JoinColumn(name = "avatar_id")
    private Media avatar;

}
