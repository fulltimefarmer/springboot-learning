package org.max.learning.common.service.impl;

import java.util.Optional;
import java.util.Set;

import org.max.learning.common.dao.UserRepository;
import org.max.learning.common.dto.User;
import org.max.learning.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public Page<User> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable);
    }

    @Cacheable(value = "users", key = "#id", cacheManager="redisCache")
    @Override
    public User findById(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        return null;
    }

    @CachePut(value = "users" , key = "#param.id", cacheManager="redisCache")
    @Override
    public Long add(User param) {
        if(null != param.getId()){
            param.setId(null);
        }
        @NonNull User saved = userRepository.save(param);
        return saved.getId();
    }

    @CachePut(value = "users", key = "#param.id", cacheManager="redisCache")
    @Override
    public User save(User param) {
        return userRepository.save(param);
    }

    @CacheEvict(value = "users", key = "#p0", allEntries = true, cacheManager="redisCache")
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Cacheable(value = "username", key = "#p0", cacheManager="redisCache")
	@Override
	public Set<String> findUsernameByType(String type) {
    	Set<String> usernameSet = userRepository.findUsernameByType(type);
    	usernameSet.stream().forEach(System.out::println);
		return usernameSet;
	}

}
