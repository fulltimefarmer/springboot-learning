package com.nike.gcsc.auth.repository;

import com.nike.gcsc.auth.dto.MiddleGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MiddleGroupPermissionRepository extends JpaRepository<MiddleGroupPermission, Long> {

    @Query(value = "DELETE FROM middle_group_permission  WHERE group_id =?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByGroupId(Long groupId);

    @Query(value = "DELETE FROM middle_group_permission  WHERE permission_id =?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByPermissionId(Long permissionId);

    @Query(value = "SELECT COUNT(*) FROM middle_group_permission  WHERE mgp.group_id=?1 AND permission_id=?2", nativeQuery = true)
    Integer countByGroupIdAndPermissionId(Long groupId, Long permissionId);

}
