package com.nike.gcsc.auth.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="middle_group_permission")
public class MiddleGroupPermission {

    public MiddleGroupPermission() {
    }

    public MiddleGroupPermission(Long groupId, Long permissionId) {
        this.groupId = groupId;
        this.permissionId = permissionId;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long permissionId;

}
