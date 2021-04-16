package com.nike.gcsc.auth.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="middle_group_user")
public class MiddleGroupUser {

    public MiddleGroupUser() {
    }

    public MiddleGroupUser(Long groupId, Long userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long userId;

}
