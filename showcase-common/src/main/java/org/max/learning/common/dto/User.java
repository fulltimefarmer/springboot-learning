package org.max.learning.common.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@Table(name="user")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

	private static final long serialVersionUID = 1251044407604334386L;

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column()
    private String password;
    
    @Column()
    private String type;
    
    @CreatedDate
    @Column(name = "create_date",nullable = true)
    private Date createDate;

    @LastModifiedDate
    @Column(name = "last_modify_date",nullable = true)
    private Date lastModifyDate;

}
