package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String all = "*";
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                            CorsConfiguration corsConfiguration = new CorsConfiguration();
                            corsConfiguration.setAllowedOriginPatterns(List.of(all));
                            corsConfiguration.setAllowedMethods(
                                    List.of(HttpMethod.GET.name(),
                                            HttpMethod.POST.name(),
                                            HttpMethod.PUT.name(),
                                            HttpMethod.DELETE.name(),
                                            HttpMethod.OPTIONS.name()));
                            corsConfiguration.setAllowCredentials(true);
                            corsConfiguration.addAllowedHeader(all);
                            return corsConfiguration;
                        }
                ))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**", "/mail/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/discount").hasAnyAuthority("Author", "Admin")
                        .requestMatchers(HttpMethod.POST, "/book").hasAnyAuthority("Admin", "Author")
                        .requestMatchers(HttpMethod.PATCH, "/book/**").hasAnyAuthority("Admin", "Author")
                        .requestMatchers(HttpMethod.PATCH, "/user/**").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.PATCH, "/author/{id}").hasAnyAuthority("Admin", "Author")
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
