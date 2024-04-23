package io.bluextech.ordika.models;
/* Created by limxuanhui on 21/4/24 */

import lombok.Data;

import java.util.List;

@Data
public class Delta<T> {

    private List<T> modified;
    private List<String> deleted;

}
