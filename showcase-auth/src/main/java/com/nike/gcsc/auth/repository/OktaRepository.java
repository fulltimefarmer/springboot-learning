package com.nike.gcsc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nike.gcsc.auth.dto.Okta;

@Repository
public interface OktaRepository extends JpaRepository<Okta, Long> {

	@Query(value = "SELECT o.* FROM okta o WHERE o.client_id = ?1", nativeQuery = true)
	Okta findByClientId(String clientId);

	@Modifying
	@Transactional
	@Query(value="DELETE FROM okta  where id in (?1)", nativeQuery = true)
	void deleteBatchByIdList(List<Long> ids);

}
