package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(min = 5, max = 20)
    private String login;
    
    @Column(nullable = false, unique = true)
    @NotBlank
    @Pattern(regexp = "^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,6}$")
    private String email;
    
    @Column(nullable = false)
    @NotBlank
    private String password;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> role;
    
    @OneToOne
    @JoinColumn(name = "author_id", nullable = true)
    private Author author;
    
    public User() {
        role = new HashSet<>();
        role.add(Role.Customer);
    }
}
