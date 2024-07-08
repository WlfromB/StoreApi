package com.example.demo.__data__;

import com.example.demo.dao.RoleRepository;
import com.example.demo.entities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.findByName("Author").isEmpty()){
            roleRepository.save(new Role("Author"));
        }
        if(roleRepository.findByName("Customer").isEmpty()){
            roleRepository.save(new Role("Customer"));
        }
        if(roleRepository.findByName("SuperUser").isEmpty()){
            roleRepository.save(new Role("SuperUser"));
        }
        if(roleRepository.findByName("Admin").isEmpty()){
            roleRepository.save(new Role("Admin"));
        }
    }
}
