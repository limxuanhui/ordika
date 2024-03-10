package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 7/1/24 */

import io.bluextech.ordika.models.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedRepository extends JpaRepository<Feed, UUID> {

    List<Feed> findFeedsByTaleId(UUID id);

}
