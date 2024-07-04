package com.example.demo.service.user;

import com.example.demo.dto.UserDto;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<User> findAll(Pageable pageable) throws Exception;
    User findById(long id) throws Exception;
    User save(UserDto user);
    void changeRole(long id, Role role) throws Exception;
}
