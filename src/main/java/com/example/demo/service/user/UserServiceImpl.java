package com.example.demo.service.user;

import com.example.demo.dao.UserRepository;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.ObjectView;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public List<User> findAll() throws Exception{
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new Exception("Users not found");
        }
        return users;
    }

    @Override
    @Transactional
    public User findById(long id) throws Exception {
        User user =  userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new Exception("User not found");
        }
        return user;
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void changeRole(long id, Role role) throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new Exception("User not found");
        }
        user.setRole(role);
        userRepository.save(user);
    }
}
