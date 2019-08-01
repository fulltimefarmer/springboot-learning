package org.max.learning.common.dao;

import java.util.Set;

import org.max.learning.common.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "SELECT u.username FROM user u WHERE u.type=?1", nativeQuery = true)
	Set<String> findUsernameByType(String type);
	
}
