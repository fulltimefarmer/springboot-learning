package com.nike.gcsc.auth.repository;

import com.nike.gcsc.auth.dto.MiddleGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MiddleGroupUserRepository extends JpaRepository<MiddleGroupUser, Long> {

    @Query(value = "DELETE FROM middle_group_user  WHERE group_id =?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByGroupId(Long groupId);

    @Query(value = "DELETE FROM middle_group_user  WHERE user_id =?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

    @Query(value = "DELETE FROM middle_group_user  WHERE group_id =?1 AND user_id =?2", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByGroupIdAndUserId(Long groupId, Long userId);

    @Query(value = "SELECT COUNT(*) FROM middle_group_user mgu WHERE mgu.group_id=?1 AND mgu.user_id=?2", nativeQuery = true)
    Integer countByGroupIdAndUserId(Long groupId, Long userId);




}
