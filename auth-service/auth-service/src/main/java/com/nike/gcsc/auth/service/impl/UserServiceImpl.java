package com.nike.gcsc.auth.service.impl;

import com.nike.gcsc.auth.constant.AuthConstant;
import com.nike.gcsc.auth.entity.User;
import com.nike.gcsc.auth.repository.GroupRepository;
import com.nike.gcsc.auth.repository.MiddleGroupUserRepository;
import com.nike.gcsc.auth.repository.UserRepository;
import com.nike.gcsc.auth.service.AuthService;
import com.nike.gcsc.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @CacheEvict(value = "userCache", allEntries = true, cacheManager="redisCache")
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

    @Caching(evict = {@CacheEvict(value = "userCache", allEntries = true, cacheManager="redisCache"), 
        @CacheEvict(value = "groupCache", allEntries = true, cacheManager="redisCache")})
    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
        middleGroupUserRepository.deleteByUserId(id);
    }

    @Caching(evict = {@CacheEvict(value = "userCache", allEntries = true, cacheManager="redisCache"), 
        @CacheEvict(value = "groupCache", allEntries = true, cacheManager="redisCache")})
    @Override
    @Transactional
    public void deleteByIdIn(List<Long> ids) {
        userRepository.deleteByIds(ids);
        middleGroupUserRepository.deleteByUserIds(ids);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "userCache", allEntries = true, cacheManager="redisCache")})
    public User edit(User param) {
        return userRepository.save(param);
    }
    
    @Override
    @Caching(evict = {@CacheEvict(value = "userCache", allEntries = true, cacheManager="redisCache")})
    public User disable(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if(opt.isPresent()) {
            User user = opt.get();
            user.setStatus(AuthConstant.USER_STATUS.DISABLE);
            userRepository.save(user);
            return user;
        }
         return null;
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
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Order.asc("username")));
        return userRepository.findAll(example, pageable);
    }

    @Override
    public User findById(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        }
        if(log.isDebugEnabled()) {
            log.debug("UserServiceImpl:findById:{}", id);
        }
        return null;
    }

    @Override
    public List<User> findByIds(List<Long> ids) {
        List<User> list = userRepository.findByIdIn(ids);
        if (null != list && list.size() > 0) {
            return list;
        }
        if(log.isDebugEnabled()) {
            log.debug("UserServiceImpl:findByIds:{}", ids.toString());
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        if(log.isDebugEnabled()) {
            log.debug("UserServiceImpl:findUserByUsername:{}", username);
        }
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
        log.info("findByProjectNameAndGroupName method begin, projectName:{},groupName:{}", projectName, groupName);
        return userRepository.findByProjectNameAndGroupName(projectName, groupName);
    }
}
