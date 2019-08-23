package com.nike.gcsc.authservice;

import com.nike.gcsc.auth.dto.Permission;
import com.nike.gcsc.auth.repository.PermissionRepository;
import com.nike.gcsc.auth.service.impl.PermissionServiceImpl;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;

/**
 * @author: tom fang
 * @date: 2019/8/15 9:54
 **/
@RunWith(PowerMockRunner.class)
public class PermissionServiceImplTest {
    @InjectMocks
    PermissionServiceImpl permissionService;
    @Mock
    PermissionRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private Permission getCommonParam(boolean hasid) {
        Permission param = new Permission();
        if (hasid) {
            param.setId(0L);
        }
        param.setName("cig");
        param.setMethod("test");
        param.setProjectId(0L);
        param.setUriRegPattern("*");
        param.setType(0);
        return param;
    }

    @Test
    public void testadd() {
        Permission dto = getCommonParam(true);
        Permission result = getCommonParam(true);
        PowerMockito.when(repository.save(dto)).thenReturn(result);
        Long id = permissionService.add(dto);
        Assert.assertEquals(id, result.getId());
    }

    @Test
    public void testdelete() {
        Permission dto = getCommonParam(true);
        PowerMockito.doNothing().when(repository).delete(dto);
        permissionService.delete(0L);
        Assert.assertTrue(true);
    }

    @Test
    public void testedit() {
        Permission dto = getCommonParam(true);
        PowerMockito.when(repository.save(dto)).thenReturn(dto);
        permissionService.edit(dto);
        Assert.assertEquals(dto.getName(), "cig");
    }

    @Test
    public void testfindAll() {
        PowerMockito.mock(Pageable.class);
        Page<Permission> page = permissionService.findAll(0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindByAttributes() {
        Permission query = new Permission();
        query.setName("xxxx");
        PowerMockito.mock(Pageable.class);
        Page<Permission> page = permissionService.findByAttributes(query, 0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindById() {
        Long id = 0L;
        Permission dto = getCommonParam(true);
        Optional<Permission> optional = Optional.of(dto);
        PowerMockito.when(repository.findById(id)).thenReturn(optional);
        Permission result = permissionService.findById(id);
        Assert.assertEquals(dto.getId(), result.getId());

        Optional opt = Optional.empty();
        PowerMockito.when(repository.findById(any())).thenReturn(opt);

        result = permissionService.findById(id);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testfindByName() {
        String name = "cig";
        Permission dto = getCommonParam(true);
        PowerMockito.when(repository.findByName(name)).thenReturn(dto);
        Permission result = permissionService.findByName(name);
        Assert.assertEquals(dto.getId(), result.getId());
    }

    @Test
    public void testfindByGroupId() {
        Permission dto = getCommonParam(true);
        List<Permission> list = Arrays.asList(dto);
        PowerMockito.when(repository.findByGroupId(any())).thenReturn(list);
        List<Permission> result = permissionService.findByGroupId(0L);
        Assert.assertEquals(list.size(), result.size());
    }

    @Test
    public void testfindExcludeByGroupId() {
        Permission dto = getCommonParam(true);
        List<Permission> list = Arrays.asList(dto);
        PowerMockito.when(repository.findExcludeByGroupId(any())).thenReturn(list);
        List<Permission> result = permissionService.findExcludeByGroupId(0L);
        Assert.assertEquals(list.size(), result.size());
    }

    @Test
    public void testfindByGroupName() {
        Permission dto = getCommonParam(true);
        List<Permission> list = Arrays.asList(dto);
        PowerMockito.when(repository.findByGroupName(any())).thenReturn(list);
        List<Permission> result = permissionService.findByGroupName(any());
        Assert.assertEquals(list.size(), result.size());
    }

    @Test
    public void testfindByProjectidAndUsername() {
        Permission dto = getCommonParam(true);
        List<Permission> list = Arrays.asList(dto);
        PowerMockito.when(repository.findByProjectidAndUsername(any(), any())).thenReturn(list);
        List<Permission> result = permissionService.findByProjectidAndUsername(any(), any());
        Assert.assertEquals(list.size(), result.size());
    }

    @Test
    public void testfindByProjectnameAndUsername() {
        Permission dto = getCommonParam(true);
        List<Permission> list = Arrays.asList(dto);
        PowerMockito.when(repository.findByProjectnameAndUsername(any(), any())).thenReturn(list);
        List<Permission> result = permissionService.findByProjectnameAndUsername(any(), any());
        Assert.assertEquals(list.size(), result.size());
    }

}
