package com.nike.gcsc.auth.controller;

import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.controller.request.PageBaseRequest;
import com.nike.gcsc.auth.entity.Project;
import com.nike.gcsc.auth.service.ProjectService;
import com.nike.gcsc.auth.utils.BeanValidationUtil;
import com.nike.gcsc.authapi.request.ProjectRequestBean;
import com.nike.gcsc.authapi.response.ProjectDto;
import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.util.BeanMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Group rest controller
 *
 * @author tom
 * @date 2019/7/15
 */
@Slf4j
@RestController
@RequestMapping("/v1/api/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @PostMapping("/check_exist_or_add")
    public GlobalResponse<Long> checkExistOrAdd(@RequestBody ProjectRequestBean bean) {
        log.debug(" Project checkExistOrAdd method param is : %s " + bean);
        Long id;
        try {
            BeanValidationUtil.checkSaveProjectBean(bean);
        } catch (Exception e) {
            return GlobalResponse.buildFail(ErrorEnum.ALREADY_EXIST,e.getMessage());
        }
        Project project = projectService.findByName(bean.getName());
        if (null != project && project.getId() > 0) {
            id = project.getId();
            if (!bean.getDescription().equals(project.getDescription())) {
                project.setDescription(bean.getDescription());
                projectService.edit(project);
            }
        } else {
            Project model = new Project();
            model.setName(bean.getName());
            model.setDescription(bean.getDescription());
            id = projectService.add(model);
        }
        return GlobalResponse.buildSuccess(id);
    }

    @DeleteMapping("/delete/{id}")
    public GlobalResponse<Boolean> delete(@PathVariable(name="id") Long id) {
        try {
            projectService.delete(id);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            log.error(String.format("delete project fail,id: {}", id), e);
            return GlobalResponse.buildSuccess(Boolean.FALSE);
        }
    }

    @PutMapping("/update/{id}")
    public GlobalResponse<Boolean> update(
            @PathVariable(name="id") Long id,
            @RequestBody ProjectRequestBean bean) {
        try {
            Project project = new Project();
            project.setId(id);
            project.setName(bean.getName());
            project.setDescription(bean.getDescription());
            projectService.edit(project);
            return GlobalResponse.buildSuccess(Boolean.TRUE);
        } catch (Exception e) {
            log.error("update project fail");
            return GlobalResponse.buildSuccess(Boolean.FALSE);
        }
    }

    @GetMapping("/find_all")
    public GlobalResponse<List<ProjectDto>> findAll() {
        List<Project> groupList = projectService.findAll(0, Integer.MAX_VALUE).getContent();
        List<ProjectDto> projectDtoList = BeanMapper.mapList(groupList, ProjectDto.class);
        return GlobalResponse.buildSuccess(projectDtoList);
    }

    @GetMapping("/find_by_name/{name}")
    public GlobalResponse<ProjectDto> findByName(@PathVariable String name) {
        ProjectDto projectDto = new ProjectDto();
        Project project = projectService.findByName(name);
        BeanMapper.copy(project, projectDto);
        return GlobalResponse.buildSuccess(projectDto);
    }

    @PostMapping("/find_by_page")
    public GlobalResponse<Page<Project>> findByPage(@RequestBody PageBaseRequest<Project> pageRequest) {
        Page<Project> result = projectService.findByAttributes(pageRequest.getOptions(),pageRequest.getPageNum(),pageRequest.getPageSize());
        return GlobalResponse.buildSuccess(result);
    }

}
