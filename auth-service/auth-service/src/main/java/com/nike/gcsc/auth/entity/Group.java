package com.nike.gcsc.auth.entity;

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
@Table(name="gcsc_group")
public class Group implements Serializable {

	private static final long serialVersionUID = 6154620636503026660L;

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String remark;

   @Column(nullable = false, name = "project_id")
   private Long projectId;
}
