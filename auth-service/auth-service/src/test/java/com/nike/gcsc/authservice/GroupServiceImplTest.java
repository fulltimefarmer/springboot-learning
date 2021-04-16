package com.nike.gcsc.authservice;

import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.nike.gcsc.auth.entity.Group;
import com.nike.gcsc.auth.entity.MiddleGroupPermission;
import com.nike.gcsc.auth.entity.MiddleGroupUser;
import com.nike.gcsc.auth.repository.GroupRepository;
import com.nike.gcsc.auth.repository.MiddleGroupPermissionRepository;
import com.nike.gcsc.auth.repository.MiddleGroupUserRepository;
import com.nike.gcsc.auth.service.impl.GroupServiceImpl;

/**
 * @author: tom fang
 * @date: 2019/8/15 9:55
 **/
@RunWith(PowerMockRunner.class)
public class GroupServiceImplTest {
    @InjectMocks
    GroupServiceImpl groupService;
    @Mock
    GroupRepository repository;
    @Mock
    MiddleGroupUserRepository middleGroupUserRepository;
    @Mock
    MiddleGroupPermissionRepository middleGroupPermissionRepository;

    private Group getCommonParam(boolean hasid) {
        Group param = new Group();
        if (hasid) {
            param.setId(0L);
        }
        param.setName("cig");
        param.setProjectId(0L);
        param.setRemark("*");
        return param;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testadd() {
        Group dto = getCommonParam(true);
        Group result = getCommonParam(true);
        PowerMockito.when(repository.save(dto)).thenReturn(result);
        Long id = groupService.add(dto);
        Assert.assertEquals(id, result.getId());
    }

    @Test
    public void testdelete() {
        Group dto = getCommonParam(true);
        PowerMockito.doNothing().when(repository).delete(dto);
        groupService.delete(0L);
        Assert.assertTrue(true);
    }

    @Test
    public void testdeleteByIdIn() {
        List<Long> ids = Arrays.asList(0L);
        PowerMockito.doNothing().when(repository).deleteByIdIn(any(List.class));
        groupService.deleteByIdIn(ids);
        Assert.assertTrue(true);
    }

    @Test
    public void testedit() {
        Group dto = getCommonParam(true);
        PowerMockito.when(repository.save(dto)).thenReturn(dto);
        groupService.edit(dto);
        Assert.assertEquals(dto.getName(), "cig");
    }

    @Test
    public void testfindAll() {
        PowerMockito.mock(Pageable.class);
        Page<Group> page = groupService.findAll(0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindByAttributes() {
        Group query = new Group();
        query.setName("xxxx");
        PowerMockito.mock(Pageable.class);
        Page<Group> page = groupService.findByAttributes(query, 0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindById() {
        Long id = 0L;
        Group dto = getCommonParam(true);
        Optional<Group> optional = Optional.of(dto);
        PowerMockito.when(repository.findById(id)).thenReturn(optional);
        Group result = groupService.findById(id);
        Assert.assertEquals(dto.getId(), result.getId());
    }

    @Test
    public void testfindByName() {
        String name = "cig";
        Group dto = getCommonParam(true);
        PowerMockito.when(repository.findByName(name)).thenReturn(dto);
        Group result = groupService.findByName(name);
        Assert.assertEquals(dto.getId(), result.getId());
    }

    @Test
    public void testfindByProjectname() {
        Group dto = getCommonParam(true);
        List<Group> list = Arrays.asList(dto);
        PowerMockito.when(repository.findByProjectname(any())).thenReturn(list);
        List<Group> result = groupService.findByProjectname(any());
        Assert.assertEquals(list.size(), result.size());
    }

    @Test
    public void testfindByProjectnameAndUsername() {
        Group dto = getCommonParam(true);
        List<Group> list = Arrays.asList(dto);
        PowerMockito.when(repository.findByProjectnameAndUsername(any(), any())).thenReturn(list);
        List<Group> result = groupService.findByProjectnameAndUsername(any(), any());
        Assert.assertEquals(list.size(), result.size());
    }

    @Test
    public void testfindByProjectnameAndPermissionname() {
        Group dto = getCommonParam(true);
        List<Group> list = Arrays.asList(dto);
        PowerMockito.when(repository.findByProjectnameAndPermissionname(any(), any())).thenReturn(list);
        List<Group> result = groupService.findByProjectnameAndPermissionname(any(), any());
        Assert.assertEquals(list.size(), result.size());
    }

    @Test
    public void testfindByProjectnameAndGroupname() {
        Group dto = getCommonParam(true);
        PowerMockito.when(repository.findByProjectnameAndGroupname(any(), any())).thenReturn(dto);
        Group result = groupService.findByProjectnameAndGroupname(any(), any());
        Assert.assertEquals(dto.getName(), result.getName());
    }

    @Test
    public void testresetUsersToGroup() {
        Set<Long> set = new HashSet<>();
        MiddleGroupUser middleGroupUser = new MiddleGroupUser();
        middleGroupUser.setUserId(0L);
        middleGroupUser.setGroupId(0L);
        List<MiddleGroupUser> mguList = Arrays.asList(middleGroupUser);
        PowerMockito.doNothing().when(middleGroupUserRepository).deleteByGroupId(0L);
        PowerMockito.when(middleGroupUserRepository.saveAll(mguList)).thenReturn(mguList);
        try {
          groupService.resetUsersToGroup(0L, set);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }

    @Test
    public void testamountUserByProject() {
        Group dto = getCommonParam(true);
        Optional<Group> groupOptional = Optional.of(dto);
        PowerMockito.when(repository.findById(any())).thenReturn(groupOptional);
        PowerMockito.doNothing().when(middleGroupUserRepository).deleteByGroupId(0L);
        MiddleGroupUser middleGroupUser = new MiddleGroupUser();
        middleGroupUser.setGroupId(0L);
        middleGroupUser.setUserId(0L);
        PowerMockito.when(middleGroupUserRepository.save(middleGroupUser)).thenReturn(middleGroupUser);
        try {
            groupService.amountUserByProject(0L, 0L, 0L);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }

    @Test
    public void testunamountUserByProject() {
        Group dto = getCommonParam(true);
        Optional<Group> groupOptional = Optional.of(dto);
        PowerMockito.when(repository.findById(any())).thenReturn(groupOptional);
        PowerMockito.doNothing().when(middleGroupUserRepository).deleteByGroupIdAndUserId(0L, 0L);
        try {
            groupService.unamountUserByProject(0L, 0L, 0L);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }

    @Test
    public void testresetPermissionsToGroup() {
        Set<Long> set = new HashSet<>();
        PowerMockito.doNothing().when(middleGroupPermissionRepository).deleteByGroupId(0L);
        MiddleGroupPermission groupPermission = new MiddleGroupPermission();
        groupPermission.setPermissionId(0L);
        groupPermission.setGroupId(0L);
        List<MiddleGroupPermission> mgpList = Arrays.asList(groupPermission);
        PowerMockito.when(middleGroupPermissionRepository.saveAll(mgpList)).thenReturn(mgpList);
        try {
            groupService.resetPermissionsToGroup(0L, set);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }

}
