package com.example.demo.service.role;

import com.example.demo.constant.classes.NotFoundConstants;
import com.example.demo.dao.RoleRepository;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role getByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException(NotFoundConstants.ROLE));
    }

    @Override
    @Transactional
    public void addUser(User user, Role role) {
        role.getUsers().add(user);
        roleRepository.save(role);
    }
}
