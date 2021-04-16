package com.nike.gcsc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nike.gcsc.auth.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query(value = "DELETE FROM gcsc_group WHERE id IN (?1)", nativeQuery = true)
    @Modifying
    void deleteByIdIn(List<Long> ids);

    @Query(value = "SELECT g.* " +
            " FROM gcsc_group g " +
            " WHERE " +
            " g.name=?1 ", nativeQuery = true)
    Group findByName(String name);

    @Query(value = "SELECT g.* " +
            " FROM gcsc_group g, gcsc_project p " +
            " WHERE " +
            " g.project_id=p.id " +
            " AND p.name=?1 ", nativeQuery = true)
    List<Group> findByProjectname(String projectname);

    @Query(value = "SELECT grp.* \n" +
            "FROM  " +
            " gcsc_group  grp ,\n" +
            " middle_group_user mgu, \n" +
            " gcsc_user usr,gcsc_project prj\n" +
            " WHERE \n" +
            " grp.id=mgu.group_id \n" +
            " AND mgu.user_id=usr.id \n" +
            " AND prj.id=grp.project_id \n" +
            " AND prj.id=?1\n" +
            " AND usr.username=?2"
            ,nativeQuery = true)
    List<Group> findByProjectidAndUsername(Long projectId,String userName);

    @Query(value = "SELECT grp.* \n" +
            "FROM  " +
            " gcsc_group  grp ,\n" +
            " middle_group_user mgu, \n" +
            " gcsc_user usr,gcsc_project prj\n" +
            " WHERE \n" +
            " grp.id=mgu.group_id \n" +
            " AND mgu.user_id=usr.id \n" +
            " AND prj.id=grp.project_id \n" +
            " AND prj.name=?1\n" +
            " AND usr.username=?2"
            ,nativeQuery = true)
    List<Group> findByProjectnameAndUsername(String projectName,String userName);

    @Query(value = "SELECT g.* \n" +
            "FROM gcsc_group g, gcsc_project p \n" +
            "WHERE \n" +
            "g.project_id=p.id \n" +
            "AND p.name=?1 \n" +
            "AND g.name=?2 ", nativeQuery = true)
    Group findByProjectnameAndGroupname(String projectname, String groupname);
    
    @Query(value = "SELECT g.* \n" +
        "FROM gcsc_group g, gcsc_project p \n" +
        "WHERE \n" +
        "g.project_id=p.id \n" +
        "AND p.id=?1 \n" +
        "AND g.name=?2 ", nativeQuery = true)
    Group findByProjectIdAndGroupname(Long projectId, String groupname);

    @Query(value = "SELECT  gp.* \n" +
            "            FROM \n" +
            "            gcsc_group gp,\n" +
            "            gcsc_project prj,\n" +
            "            middle_group_permission mgp,\n" +
            "            gcsc_permission ps \n" +
            "            WHERE \n" +
            "            mgp.permission_id=ps.id \n" +
            "            AND mgp.group_id=gp.id \n" +
            "            AND prj.id=gp.project_id \n" +
            "            AND prj.name=?1" +
            "            AND ps.name=?2 \n", nativeQuery = true)
    List<Group> findByProjectnameAndPermissionname(String projectName, String permissionName);

}
