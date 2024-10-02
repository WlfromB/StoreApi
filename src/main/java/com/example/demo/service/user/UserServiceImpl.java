package com.example.demo.service.user;

import com.example.demo.constant.classes.NotFoundConstants;
import com.example.demo.constant.classes.RolesConstants;
import com.example.demo.dao.RoleRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.UserDto;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.security.PasswordProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
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
    private final ApplicationContext applicationContext;
    
    @Override
    @Transactional
    public Page<User> findAll(Pageable pageable) throws Exception {
        Page<User> users = userRepository.findAll(pageable);
        if (users.isEmpty()) {
            throw new NotFoundException(NotFoundConstants.setMany(NotFoundConstants.USER));
        }
        return users;
    }

    @Override
    @Transactional
    public User findById(long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundConstants.USER));
    }

    @Override
    @Transactional
    public User save(UserDto user) throws NotFoundException {
        User userEntity = user.from();
        userEntity.setPassword(passwordProvider.getPassword(user.getPassword()));
        if (userEntity.getRoles() == null) {
            Role role = roleRepository.findByName(RolesConstants.CUSTOMER)
                    .orElseThrow(() -> new NotFoundException(NotFoundConstants.ROLE));
            HashSet<Role> roles = new HashSet<>();
            roles.add(role);
            userEntity.setRoles(roles);
            role.getUsers().add(userEntity);
        }
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void changeRole(long id, Role role) throws NotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundConstants.USER));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User findByEmailOrLogin(String emailOrLogin) throws NotFoundException {
        return userRepository.findUserByEmailOrLogin(emailOrLogin, emailOrLogin)
                .orElseThrow(() -> new NotFoundException(NotFoundConstants.USER));
    }

    @Override
    @Transactional
    public boolean setActivatedMail(String email) throws Exception {
        UserService userService = applicationContext.getBean(UserService.class);  
        User user = userService.findByEmailOrLogin(email);
        user.setEmailVerified(ACTIVATED);
        userRepository.save(user);
        return ACTIVATED;
    }
}
