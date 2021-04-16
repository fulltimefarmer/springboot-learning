package com.nike.gcsc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nike.gcsc.auth.entity.Okta;

@Repository
public interface OktaRepository extends JpaRepository<Okta, Long> {

	@Modifying
	@Query(value="DELETE FROM okta  where id in (?1)", nativeQuery = true)
	void deleteBatchByIdList(List<Long> ids);

	@Query(value = "SELECT o.* FROM okta o WHERE o.client_id = ?1", nativeQuery = true)
	Okta findByClientId(String clientId);
	
	@Query(value = "SELECT o.* FROM okta o WHERE o.describe = ?1", nativeQuery = true)
	Okta findByDescribe(String describe);
	
	@Query(value = "SELECT o.* \n" + 
			"FROM auth.okta o, auth.gcsc_project p \n" + 
			"WHERE \n" + 
			"o.project_id = p.id \n" + 
			"AND p.name = ?1", nativeQuery = true)
	List<Okta> findByProjectName(String projectName);
	
}
