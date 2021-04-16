package com.nike.gcsc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nike.gcsc.auth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "DELETE FROM gcsc_user WHERE id IN (?1)", nativeQuery = true)
    @Modifying
    void deleteByIds(List<Long> ids);

    @Query(value = "SELECT * FROM gcsc_user WHERE username =?1", nativeQuery = true)
    User findByUsername(String username);

    @Query(value = "SELECT u.* FROM gcsc_user u WHERE u.id IN(?1)", nativeQuery = true)
    List<User> findByIdIn(List<Long> ids);

    @Query(value = "SELECT u.* FROM gcsc_user u, middle_group_user gu WHERE u.id = gu.user_id AND gu.group_id = ?1", nativeQuery = true)
    List<User> findByGroupId(Long groupId);

    @Query(value = "SELECT DISTINCT u.* \n" +
            "FROM gcsc_user u \n" +
            "LEFT JOIN middle_group_user mgu ON u.id = mgu.user_id\n" +
            "WHERE 1=1\n" +
            "AND mgu.group_id <> ?1 OR mgu.group_id IS NULL", nativeQuery = true)
    List<User> findByExcludeGroupId(Long groupId);

    @Query(value = "SELECT DISTINCT u.* \n" +
            "FROM gcsc_user u, gcsc_group g, gcsc_project p, middle_group_user mgu \n" +
            "WHERE \n" +
            "mgu.user_id = u.id \n" +
            "AND mgu.group_id = g.id \n" +
            "AND g.project_id = p.id \n" +
            "AND p.name=?1 \n" +
            "AND g.id=?2",
            nativeQuery = true)
    List<User> findByProjectNameAndGroupId(String projectName, Long groupId);

    @Query(value = "SELECT DISTINCT u.* \n" +
            "FROM gcsc_user u, gcsc_group g, gcsc_project p, middle_group_user mgu \n" +
            "WHERE \n" +
            "mgu.user_id = u.id \n" +
            "AND mgu.group_id = g.id \n" +
            "AND g.project_id = p.id \n" +
            "AND p.name=?1 \n" +
            "AND g.name=?2",
            nativeQuery = true)
    List<User> findByProjectNameAndGroupName(String projectName, String groupName);

}

