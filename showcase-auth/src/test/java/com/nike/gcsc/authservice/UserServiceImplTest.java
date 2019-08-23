package com.nike.gcsc.authservice;

import com.nike.gcsc.auth.constant.UserTypeEnum;
import com.nike.gcsc.auth.dto.User;
import com.nike.gcsc.auth.repository.UserRepository;
import com.nike.gcsc.auth.service.impl.UserServiceImpl;
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
 * @date: 2019/8/12 14:43
 **/
@RunWith(PowerMockRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private User getCommonParam() {
        User param = new User();
        param.setType(UserTypeEnum.NIKE_USER.getCode());
        param.setEmail("tom.fang@nike.com");
        param.setUsername("tom.fang@nike.com");
        param.setDisplayName("tom.fang@nike.com");
        param.setPassword("123456");
        return param;
    }

    @Test
    public void testadd() {
        User param = getCommonParam();
        User retrunParam = param;
        retrunParam.setId(100L);
        PowerMockito.when(userRepository.save(param)).thenReturn(retrunParam);
        Long id = userService.add(param);
        Assert.assertEquals(retrunParam.getId(), id);
    }
    @Test
    public void testcheckNikeUserExistOrAdd() {
        User param = getCommonParam();
        User retrunParam = param;
        retrunParam.setId(100L);
        PowerMockito.when(userRepository.findByUsername(any())).thenReturn(null);
        PowerMockito.when(userRepository.save(param)).thenReturn(retrunParam);
        userService.checkNikeUserExistOrAdd(any());
        Assert.assertNotNull(retrunParam.getId());
    }

    @Test
    public void testdelete() {
        Long id = 0L;
        try {
            PowerMockito.doNothing().when(userRepository).deleteById(id);
            userService.delete(id);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testdeleteByIdIn() {
        List<Long> ids = Arrays.asList(0L);
        PowerMockito.doNothing().when(userRepository).deleteByIds(ids);
        try {
            userService.deleteByIdIn(ids);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testedit() {
        User param = getCommonParam();
        PowerMockito.when(userRepository.save(param)).thenReturn(param);
        User returnValue = userService.edit(param);
        Assert.assertEquals(param.getUsername(), returnValue.getUsername());
    }

    @Test
    public void testfindAll() {
        PowerMockito.mock(Pageable.class);
        Page<User> page = userService.findAll(0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindByAttributes() {
        User query = new User();
        query.setUsername("xxxx");
        PowerMockito.mock(Pageable.class);
        Page<User> page = userService.findByAttributes(query, 0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void testfindById() {
        Long id = 0L;
        User user = getCommonParam();
        Optional<User> optionalUser = Optional.of(user);
        PowerMockito.when(userRepository.findById(id)).thenReturn(optionalUser);
        User result = userService.findById(id);
        Assert.assertEquals(user.getId(), result.getId());

        Optional opt = Optional.empty();
        PowerMockito.when(userRepository.findById(any())).thenReturn(opt);

        result = userService.findById(id);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testfindByIds() {
        List<Long> ids = Arrays.asList(0L);
        List<User> list = Arrays.asList(getCommonParam());
        PowerMockito.when(userRepository.findByIdIn(ids)).thenReturn(list);
        List<User> userList = userService.findByIds(ids);
        Assert.assertEquals(list.size(), userList.size());

        PowerMockito.when(userRepository.findByIdIn(ids)).thenReturn(null);
        userList = userService.findByIds(ids);
        Assert.assertEquals(null, userList);
    }

    @Test
    public void testfindByUsername() {
        User user = getCommonParam();
        PowerMockito.when(userRepository.findByUsername(any())).thenReturn(user);
        User result = userService.findByUsername(any());
        Assert.assertEquals(user.getUsername(), result.getUsername());

        PowerMockito.when(userRepository.findByUsername(any())).thenReturn(null);
        result = userService.findByUsername(any());
        Assert.assertEquals(null, result);
    }

    @Test
    public void testfindByGroupId() {
        User user = getCommonParam();
        List<User> list = Arrays.asList(user);
        PowerMockito.when(userRepository.findByGroupId(any())).thenReturn(list);
        List<User> userList = userService.findByGroupId(any());
        Assert.assertEquals(list.size(), userList.size());
    }

    @Test
    public void testfindExcludeByGroupId() {
        Long groupId = 0L;
        User user = getCommonParam();
        List<User> list = Arrays.asList(user);
        PowerMockito.when(userRepository.findByExcludeGroupId(any())).thenReturn(list);
        List<User> userList = userService.findExcludeByGroupId(groupId);
        Assert.assertEquals(list.size(), userList.size());
    }

    @Test
    public void testfindByProjectNameAndGroupId() {
        User user = getCommonParam();
        List<User> list = Arrays.asList(user);
        PowerMockito.when(userRepository.findByProjectNameAndGroupId(any(), any())).thenReturn(list);
        List<User> userList = userService.findByProjectNameAndGroupId(any(), any());
        Assert.assertEquals(list.size(), userList.size());
    }

    @Test
    public void findByProjectNameAndGroupName() {
        User user = getCommonParam();
        List<User> list = Arrays.asList(user);
        PowerMockito.when(userRepository.findByProjectNameAndGroupName(any(), any())).thenReturn(list);
        List<User> userList = userService.findByProjectNameAndGroupName(any(), any());
        Assert.assertEquals(list.size(), userList.size());
    }

}
