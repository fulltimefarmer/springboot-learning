package org.max.learning.common.service;


import java.util.Set;

import org.max.learning.common.dto.User;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<User> findAll(Integer pageNumber, Integer pageSize);
    User findById(Long id);
    Long add(User param);
    User save(User param);
    void delete(Long id);

    Set<String> findUsernameByType(String type);
    
}
