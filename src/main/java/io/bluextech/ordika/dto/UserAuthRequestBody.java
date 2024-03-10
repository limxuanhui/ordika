package io.bluextech.ordika.dto;
/* Created by limxuanhui on 7/1/24 */

import io.bluextech.ordika.models.User;
import lombok.Data;

@Data
public class UserAuthRequestBody {

    private User user;
    private String idToken;

}
