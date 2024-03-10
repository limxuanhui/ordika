package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 5/1/24 */

import io.bluextech.ordika.models.Tale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaleRepository extends JpaRepository<Tale, UUID> {

}
