package com.nike.gcsc.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nike.gcsc.auth.dto.Project;
import com.nike.gcsc.auth.repository.ProjectRepository;
import com.nike.gcsc.auth.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Override
    public Long add(Project param) {
        param.setId(null);
        Project saved = projectRepository.save(param);
        return saved.getId();
    }

    @CacheEvict(value = "projectCache", allEntries = true, cacheManager="caffeineCache")
    @Override
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public void edit(Project param) {
        projectRepository.save(param);
    }

    @Override
    public Page<Project> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return projectRepository.findAll(pageable);
    }

    @Override
    public Page<Project> findByAttributes(Project queryBean, Integer pageNumber, Integer pageSize) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        Example<Project> example = Example.of(queryBean, matcher);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return projectRepository.findAll(example, pageable);
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id).orElse(new Project());
    }

    @Cacheable(value = "projectCache", key = "#p0", cacheManager="caffeineCache",unless = "#result==null")
    @Override
    public Project findByName(String name) {
        return projectRepository.findByName(name);
    }

    @Override
    public Project findByGroupName(String groupName) {
        return projectRepository.findByGroupName(groupName);
    }

    @Override
    public Project findByPermissionName(String permissionName) {
        return projectRepository.findByPermissionName(permissionName);
    }

}
