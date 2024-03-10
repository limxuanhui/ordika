package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 7/1/24 */

import io.bluextech.ordika.models.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

}
