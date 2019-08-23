package com.nike.gcsc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nike.gcsc.auth.dto.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query(value = "delete FROM gcsc_permission WHERE id in (?1)", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByIds(List<Long> ids);

    @Query(value = "SELECT p.* FROM gcsc_permission p WHERE p.name = ?1", nativeQuery = true)
    Permission findByName(String name);

    @Query(value = "SELECT p.* FROM gcsc_permission p, middle_group_permission gp WHERE p.id = gp.permission_id AND gp.group_id = ?1", nativeQuery = true)
    List<Permission> findByGroupId(Long groupId);

    @Query(value = "SELECT u.*\n" +
            "FROM gcsc_permission u \n" +
            "LEFT JOIN middle_group_permission mgu ON u.id = mgu.permission_id\n" +
            "WHERE 1=1\n" +
            "AND mgu.group_id <> ?1 OR mgu.group_id IS NULL", nativeQuery = true)
    List<Permission> findExcludeByGroupId(Long groupId);

    @Query(value = "SELECT \n" +
            "ps.* \n" +
            "FROM \n" +
            "middle_group_permission mgp,\n" +
            "gcsc_permission ps,gcsc_group gp \n" +
            "WHERE \n" +
            "mgp.permission_id=ps.id \n" +
            "AND mgp.group_id=gp.id \n" +
            "AND gp.name=?1 ",nativeQuery = true)
    List<Permission> findByGroupName(String groupName);

    @Query(value = "SELECT \n" +
            "p.* \n" +
            "FROM \n" +
            "middle_group_permission mgp, \n" +
            "gcsc_permission p, \n" +
            "gcsc_group g, \n" +
            "middle_group_user mgu, \n" +
            "gcsc_user u, \n" +
            "gcsc_project pro \n" +
            "WHERE \n" +
            "u.id=mgu.user_id \n" +
            "AND mgu.group_id=g.id \n" +
            "AND g.id=mgp.group_id \n" +
            "AND mgp.permission_id=p.id \n" +
            "AND g.project_id=pro.id \n" +
            "AND pro.id=?1 \n" +
            "AND u.username=?2", nativeQuery = true)
    List<Permission> findByProjectidAndUsername(Long projectId, String username);

    @Query(value = "SELECT \n" +
            "p.* \n" +
            "FROM \n" +
            "middle_group_permission mgp, \n" +
            "gcsc_permission p, \n" +
            "gcsc_group g, \n" +
            "middle_group_user mgu, \n" +
            "gcsc_user u, \n" +
            "gcsc_project pro \n" +
            "WHERE \n" +
            "u.id=mgu.user_id \n" +
            "AND mgu.group_id=g.id \n" +
            "AND g.id=mgp.group_id \n" +
            "AND mgp.permission_id=p.id \n" +
            "AND g.project_id=pro.id \n" +
            "AND pro.name=?1 \n" +
            "AND u.username=?2", nativeQuery = true)
    List<Permission> findByProjectnameAndUsername(String projectName, String username);

}
