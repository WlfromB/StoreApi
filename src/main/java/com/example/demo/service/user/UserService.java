package com.example.demo.service.user;

import com.example.demo.entities.Role;
import com.example.demo.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAll() throws Exception;
    User findById(long id) throws Exception;
    void save(User user);
    void changeRole(long id, Role role) throws Exception;
}
