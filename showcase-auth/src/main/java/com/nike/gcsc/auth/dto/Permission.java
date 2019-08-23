package com.nike.gcsc.auth.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="gcsc_permission")
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String uriRegPattern;

    private String remark;

    @Column(name="project_id")
    private Long projectId;

}
