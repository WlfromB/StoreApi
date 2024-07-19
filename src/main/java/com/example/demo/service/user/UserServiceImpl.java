package com.example.demo.service.user;

import com.example.demo.dao.RoleRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.UserDto;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.security.PasswordProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordProvider passwordProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Page<User> findAll(Pageable pageable) throws Exception {
        Page<User> users = userRepository.findAll(pageable);
        if (users.isEmpty()) {
            throw new NotFoundException("Users not found");
        }
        log.info("Users found {}", users);
        return users;
    }

    @Override
    @Transactional
    public User findById(long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User save(UserDto user) throws Exception {
        User userEntity = user.from();
        userEntity.setPassword(passwordProvider.getPassword(user.getPassword()));
        if (userEntity.getRoles() == null) {
            Role role = roleRepository.findByName("Customer")
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            HashSet<Role> roles = new HashSet<>();
            roles.add(role);
            userEntity.setRoles(roles);
            role.getUsers().add(userEntity);
        }
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void changeRole(long id, Role role) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User findByEmailOrLogin(String emailOrLogin) throws Exception {
        return userRepository.findUserByEmailOrLogin(emailOrLogin, emailOrLogin)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
