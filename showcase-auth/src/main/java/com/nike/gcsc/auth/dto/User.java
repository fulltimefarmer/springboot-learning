package com.nike.gcsc.auth.dto;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="gcsc_user")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer type;
    
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String password;
    
    @Column(nullable = true)
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = true)
    private String remark;

    @CreatedDate
    @Column(name = "create_date",nullable = true)
    private Date createDate;

    @LastModifiedDate
    @Column(name = "last_modify_date",nullable = true)
    private Date lastModifyDate;

}
