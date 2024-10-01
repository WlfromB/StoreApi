package com.example.demo.security;

import com.example.demo.constant.classes.RolesConstants;
import com.example.demo.constant.classes.URIStartWith;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${springdoc.api-docs.path}")
    private String V3_DOCS_PATH;

    @Value("${springdoc.swagger-ui.path}")
    private String SWAGGER_UI_PATH;

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
                        .requestMatchers(URIStartWith.getAllAuthorities(URIStartWith.AUTH),
                                URIStartWith.getAllAuthorities(URIStartWith.MAIL)).permitAll()
                        .requestMatchers(HttpMethod.POST, URIStartWith.USER).permitAll()
                        .requestMatchers(URIStartWith.getAllAuthorities(V3_DOCS_PATH),
                                URIStartWith.getAllAuthorities(SWAGGER_UI_PATH)).permitAll()
                        .requestMatchers(HttpMethod.POST, URIStartWith.DISCOUNT)
                        .hasAnyAuthority(RolesConstants.AUTHOR, RolesConstants.ADMIN)
                        .requestMatchers(HttpMethod.POST, URIStartWith.BOOK)
                        .hasAnyAuthority(RolesConstants.AUTHOR, RolesConstants.ADMIN)
                        .requestMatchers(HttpMethod.PATCH, URIStartWith.getAllAuthorities(URIStartWith.BOOK))
                        .hasAnyAuthority(RolesConstants.AUTHOR, RolesConstants.ADMIN)
                        .requestMatchers(HttpMethod.PATCH, URIStartWith.getAllAuthorities(URIStartWith.USER))
                        .hasAuthority(RolesConstants.ADMIN)
                        .requestMatchers(HttpMethod.PATCH, URIStartWith.getAddedIdParam(URIStartWith.AUTHOR))
                        .hasAnyAuthority(RolesConstants.AUTHOR, RolesConstants.ADMIN)
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
