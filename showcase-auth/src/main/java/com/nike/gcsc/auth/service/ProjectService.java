package com.nike.gcsc.auth.service;

import com.nike.gcsc.auth.dto.Project;
import org.springframework.data.domain.Page;

public interface ProjectService {

    Long add(Project param);

    void delete(Long id);

    void edit(Project param);

    Page<Project> findAll(Integer pageNumber, Integer pageSize);
    Page<Project> findByAttributes(Project queryBean, Integer pageNumber, Integer pageSize);
    Project findById(Long id);
    Project findByName(String name);
    Project findByGroupName(String groupName);
    Project findByPermissionName(String permissionName);

}
