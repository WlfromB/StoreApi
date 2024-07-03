package com.example.demo.service.user;

import com.example.demo.dao.UserRepository;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.ObjectView;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public Page<User> findAll(Pageable pageable) throws Exception{
        Page<User> users = userRepository.findAll(pageable);
        if (users.isEmpty()) {
            throw new Exception("Users not found");
        }
        log.info("Users found {}", users);
        return users;
    }

    @Override
    @Transactional
    public User findById(long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(()->new Exception("User not found"));
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void changeRole(long id, Role role) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(()->new Exception("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }
}
