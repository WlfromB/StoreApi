package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import javax.naming.Name;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String login;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @OneToOne
    @JoinColumn(name = "author_id", nullable = true)
    private Author author;
    
    public User() {
        role = Role.Customer;
    }
    
    public void changeRole(Role role){
        if(this.role.ordinal() < role.ordinal()){
            this.role = role;
        }
    }
}
