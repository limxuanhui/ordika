package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 13/7/23 */

import io.bluextech.ordika.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
