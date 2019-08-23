package com.nike.gcsc.auth.service.impl;

import com.nike.gcsc.auth.dto.User;
import com.nike.gcsc.auth.repository.GroupRepository;
import com.nike.gcsc.auth.repository.MiddleGroupUserRepository;
import com.nike.gcsc.auth.repository.UserRepository;
import com.nike.gcsc.auth.service.AuthService;
import com.nike.gcsc.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MiddleGroupUserRepository middleGroupUserRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    AuthService oktaService;

    @Override
    public Long add(User param) {
        param.setId(null);
        User saved = userRepository.save(param);
        return saved.getId();
    }

    @Override
    public Long checkNikeUserExistOrAdd(String username) {
        User user = userRepository.findByUsername(username);
        if(null == user){
            user = new User();
            user.setUsername(username);
            user.setEmail(username);
            user.setType(1);
            userRepository.save(user);
        }
        return user.getId();
    }

    @CacheEvict(value = "userCache", allEntries = true, cacheManager="redisCache")
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @CacheEvict(value = "userCache", allEntries = true, cacheManager="redisCache")
    @Override
    public void deleteByIdIn(List<Long> ids) {
        userRepository.deleteByIds(ids);
    }

    @Override
    public User edit(User param) {
        return userRepository.save(param);
    }

    @Override
    public Page<User> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> findByAttributes(User queryBean, Integer pageNumber, Integer pageSize) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        Example<User> example = Example.of(queryBean, matcher);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(example, pageable);
    }

    @Override
    public User findById(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        }
        log.debug("UserServiceImpl:findById:{}", id);
        return null;
    }

    @Override
    public List<User> findByIds(List<Long> ids) {
        List<User> list = userRepository.findByIdIn(ids);
        if (null != list && list.size() > 0) {
            return list;
        }
        log.debug("UserServiceImpl:findByIds:{}", ids.toString());
        return null;
    }

    @Override
    public User findByUsername(String username) {
        log.debug("UserServiceImpl:findUserByUsername:{}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByGroupId(Long groupId) {
        return userRepository.findByGroupId(groupId);
    }

    @Override
    public List<User> findExcludeByGroupId(Long groupId) {
        return userRepository.findByExcludeGroupId(groupId);
    }

    @Override
    public List<User> findByProjectNameAndGroupId(String projectName, Long groupId) {
        return userRepository.findByProjectNameAndGroupId(projectName, groupId);
    }

    @Cacheable(value = "userCache", key = "#p0.concat(':').concat(#p1)", cacheManager="redisCache", unless = "#result==null")
    @Override
    public List<User> findByProjectNameAndGroupName(String projectName, String groupName) {
        return userRepository.findByProjectNameAndGroupName(projectName, groupName);
    }

}
