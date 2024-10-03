package com.example.demo.service.role;

import com.example.demo.entities.Role;
import com.example.demo.entities.User;

public interface RoleService {
    Role getByName(String roleName);

    void addUser(User user, Role role);
}
