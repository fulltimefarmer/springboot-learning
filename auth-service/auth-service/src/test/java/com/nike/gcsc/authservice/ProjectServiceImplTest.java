package com.nike.gcsc.authservice;

import com.nike.gcsc.auth.entity.Project;
import com.nike.gcsc.auth.repository.ProjectRepository;
import com.nike.gcsc.auth.service.impl.ProjectServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.Matchers.any;

/**
 * @author: tom fang
 * @date: 2019/8/14 16:49
 **/
@RunWith(PowerMockRunner.class)
public class ProjectServiceImplTest {

    @InjectMocks
    ProjectServiceImpl projectService;
    @Mock
    ProjectRepository projectRepository;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private Project getCommonParam(boolean hasid) {
        Project param = new Project();
        if (hasid) {
            param.setId(0L);
        }
        param.setName("cig");
        param.setDescription("test");
        return param;
    }

    @Test
    public void testadd() {
        Project project = getCommonParam(false);
        Project result = getCommonParam(true);
        PowerMockito.when(projectRepository.save(project)).thenReturn(result);
        Long id = projectService.add(project);
        Assert.assertEquals(id, result.getId());
    }

    @Test
    public void testdelete() {
        Project project = getCommonParam(true);
        PowerMockito.doNothing().when(projectRepository).delete(project);
        projectService.delete(0L);
        Assert.assertTrue(true);
    }

    @Test
    public void testedit() {
        Project param = getCommonParam(true);
        PowerMockito.when(projectRepository.save(param)).thenReturn(param);
        projectService.edit(param);
        Assert.assertEquals(param.getName(), "cig");
    }

    @Test
    public void testfindAll() {
        PowerMockito.mock(Pageable.class);
        Page<Project> page = projectService.findAll(0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindByAttributes() {
        Project query = new Project();
        query.setName("xxxx");
        PowerMockito.mock(Pageable.class);
        Page<Project> page = projectService.findByAttributes(query, 0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindById() {
        Long id = 0L;
        Project project = getCommonParam(true);
        Optional<Project> optional = Optional.of(project);
        PowerMockito.when(projectRepository.findById(id)).thenReturn(optional);
        Project result = projectService.findById(id);
        Assert.assertEquals(project.getId(), result.getId());

        Optional opt = Optional.empty();
        PowerMockito.when(projectRepository.findById(any())).thenReturn(opt);

        result = projectService.findById(id);
        Assert.assertEquals(null, result.getId());
    }

    @Test
    public void testfindByName() {
        Project project = getCommonParam(true);
        PowerMockito.when(projectRepository.findByName(any())).thenReturn(project);
        Project result = projectService.findByName(any());
        Assert.assertEquals(project.getName(), result.getName());

        PowerMockito.when(projectRepository.findByName(any())).thenReturn(null);
        result = projectService.findByName(any());
        Assert.assertEquals(null, result);
    }

    @Test
    public void testfindByGroupName() {
        Project project = getCommonParam(true);
        PowerMockito.when(projectRepository.findByGroupName(any())).thenReturn(project);
        Project result = projectService.findByGroupName(any());
        Assert.assertEquals(project.getName(), result.getName());
    }

    @Test
    public void testfindByPermissionName() {
        Project project = getCommonParam(true);
        PowerMockito.when(projectRepository.findByPermissionName(any())).thenReturn(project);
        Project result = projectService.findByPermissionName(any());
        Assert.assertEquals(project.getName(), result.getName());
    }

}
