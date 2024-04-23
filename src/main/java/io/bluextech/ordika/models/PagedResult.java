package io.bluextech.ordika.models;
/* Created by limxuanhui on 12/3/24 */

import java.util.List;

public record PagedResult<T>(List<T> items, String lastEvaluatedKey) {}
