package com.example.demo.controllers;

import com.example.demo.requests.JwtRequest;
import com.example.demo.requests.RefreshRequest;
import com.example.demo.responses.JwtResponse;
import com.example.demo.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.webjars.NotFoundException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void loginShouldReturnNotFound() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLoginOrEmail("email@email.com");
        request.setPassword("password");
        doThrow(new NotFoundException("not found")).when(authService).login(request);

        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void loginShouldReturnBadRequest() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLoginOrEmail("email@email.com");
        String jsonString = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/auth/login")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginShouldReturnOk() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLoginOrEmail("email@email.com");
        request.setPassword("password");
        JwtResponse response = new JwtResponse();
        response.setAccessToken("access-token");
        response.setRefreshToken("refresh-token");
        when(authService.login(request)).thenReturn(response);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
        verify(authService).login(request);
    }

    @Test
    void accessShouldReturnOk() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setToken("refresh-token");
        String accessToken = "access-token";
        JwtResponse response = new JwtResponse();
        response.setRefreshToken(accessToken);
        when(authService.getAccessToken(request.getToken())).thenReturn(response);
        mockMvc.perform(post("/auth/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
        verify(authService).getAccessToken(request.getToken());
    }

    @Test
    void accessShouldReturnForbidden() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setToken("refresh-token");
        String accessToken = "access-token";
        JwtResponse response = new JwtResponse();
        response.setRefreshToken(accessToken);
        doThrow(new AccessDeniedException("not found")).when(authService).getAccessToken(request.getToken());
        mockMvc.perform(post("/auth/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
        verify(authService).getAccessToken(request.getToken());
    }

    @Test
    void refreshShouldReturnOk() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setToken("refresh-token");
        String accessToken = "access-token";
        JwtResponse response = new JwtResponse();
        response.setRefreshToken(accessToken);
        response.setRefreshToken("refresh-token");
        when(authService.refresh(request.getToken())).thenReturn(response);
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
        verify(authService).refresh(request.getToken());
    }

    @Test
    void refreshShouldReturnForbidden() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setToken("refresh-token");
        String accessToken = "access-token";
        JwtResponse response = new JwtResponse();
        response.setRefreshToken(accessToken);
        response.setRefreshToken("refresh-token");
        doThrow(new AccessDeniedException("not found")).when(authService).refresh(request.getToken());
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
        verify(authService).refresh(request.getToken());
    }
}
