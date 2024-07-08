package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.Manager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final PasswordProvider passwordProvider;
    
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors-> cors.configurationSource(request -> {
                     CorsConfiguration corsConfiguration = new CorsConfiguration();
                     corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                     corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                     corsConfiguration.setAllowCredentials(true);
                     corsConfiguration.addAllowedHeader("*");
                     return corsConfiguration;
                    }
                    ))
                    .authorizeHttpRequests(request-> request
                            .requestMatchers("/auth/**", "/user").permitAll()
                            .requestMatchers(HttpMethod.POST, "/discount").hasAnyAuthority("Author", "Admin")
                            .requestMatchers("/book/**").hasAuthority("Admin")
                            .anyRequest().authenticated())
                    .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }
}
