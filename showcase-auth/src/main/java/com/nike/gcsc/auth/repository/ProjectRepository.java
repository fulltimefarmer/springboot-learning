package com.nike.gcsc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nike.gcsc.auth.dto.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = "SELECT p.* FROM gcsc_project p WHERE p.name =?1", nativeQuery = true)
    Project findByName(String projectName);

    @Query(value = "SELECT proj.* FROM gcsc_group gp, gcsc_project proj WHERE gp.project_id=proj.id AND gp.name=?1", nativeQuery = true)
    Project findByGroupName(String groupName);

    @Query(value = "SELECT proj.* FROM gcsc_permission ps, gcsc_project proj WHERE ps.project_id=proj.id AND ps.name=?1", nativeQuery = true)
    Project findByPermissionName(String permissionName);

    @Query(value = "SELECT p.* FROM gcsc_project p, okta o WHERE o.project_id=p.id AND o.client_id=?1", nativeQuery = true)
    List<Project> findProjectnameByClientId(String clientId);

}
