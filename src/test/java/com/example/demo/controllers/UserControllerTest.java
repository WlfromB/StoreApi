package com.example.demo.controllers;

import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.auth.JwtProvider;
import com.example.demo.service.author.AuthorService;
import com.example.demo.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PageableCreator pageableCreator;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createUserInvalidDtoShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void createUserBlankEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("");
        userDto.setLogin("olezhka");
        userDto.setPassword("passwordddd");
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void createUserValidDtoShouldReturnCreated() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setLogin("olezhka");
        userDto.setPassword("passwordddd");
        when(userService.save(userDto)).thenReturn(userDto.from());
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/user/"))
                .andDo(print());
        verify(userService, times(1)).save(userDto);
    }

    @Test
    void getUsersWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/user/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUsersWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<User> userPage = new PageImpl<>(Collections.singletonList(mockUser), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(userService.findAll(pageable)).thenReturn(userPage);

        mockMvc.perform(get("/user/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.content[0].id").value(mockUser.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).findAll(pageable);
        verify(pageableCreator, times(1)).create(paginationParams);
    }

    @Test
    void getUsersWithTokenShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        doThrow(NotFoundException.class).when(userService).findAll(pageable);

        mockMvc.perform(get("/user/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findAll(pageable);
        verify(pageableCreator, times(1)).create(paginationParams);
    }

    @Test
    void getUserWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserWithTokenShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);
        doThrow(NotFoundException.class).when(userService).findById(anyLong());
        mockMvc.perform(get("/user")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("id", String.valueOf(anyLong()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(anyLong());
    }

    @Test
    void getUserWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        when(userService.findById(mockUser.getId())).thenReturn(mockUser);

        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("id", String.valueOf(mockUser.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()));

        verify(userService, times(1)).findById(mockUser.getId());
    }

    @Test
    void createAuthorWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Oleg");
        author.setLastName("Smirnov");
        author.setDateOfBirthday(LocalDate.of(1970, 12, 20));
        author.setBooks(Collections.emptySet());

        AuthorDto authorDto = new AuthorDto(author);

        when(authorService.saveAuthor(authorDto, mockUser.getId())).thenReturn(author);

        mockMvc.perform(post("/user/{id}", mockUser.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(author.getId()))
                .andExpect(header().string("Location", "http://localhost/user/1"))
                .andDo(print());
        verify(authorService, times(1)).saveAuthor(authorDto, mockUser.getId());
    }

    @Test
    void createAuthorWithoutTokenShouldReturnForbidden() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Oleg");
        author.setLastName("Smirnov");
        author.setDateOfBirthday(LocalDate.of(1970, 12, 20));
        author.setBooks(Collections.emptySet());

        AuthorDto authorDto = new AuthorDto(author);
        mockMvc.perform(post("/user/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void createAuthorWithIllegalArgUserIdShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Oleg");
        author.setLastName("Smirnov");
        author.setDateOfBirthday(LocalDate.of(1970, 12, 20));
        author.setBooks(Collections.emptySet());

        AuthorDto authorDto = new AuthorDto(author);

        doThrow(NotFoundException.class).when(authorService).saveAuthor(authorDto, mockUser.getId());

        mockMvc.perform(post("/user/{id}", mockUser.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andExpect(status().isNotFound())
                .andDo(print());
        verify(authorService, times(1)).saveAuthor(authorDto, mockUser.getId());
    }

    @Test
    void createAuthorWithTokenShouldReturnBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Oleg");
        author.setLastName("Smirnov");
        author.setDateOfBirthday(LocalDate.of(1970, 12, 20));
        author.setBooks(Collections.emptySet());

        AuthorDto authorDto = new AuthorDto();
        authorDto.setLastName("");
        authorDto.setFirstName("");
        authorDto.setDateOfBirthday(LocalDate.of(1970, 12, 20));

        when(authorService.saveAuthor(authorDto, mockUser.getId())).thenReturn(author);

        mockMvc.perform(post("/user/{id}", mockUser.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void updateUserShouldAddRole() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Admin")));
        String token = jwtProvider.generateAccessToken(mockUser);
        Role role = new Role("Admin");

        doNothing().when(userService).changeRole(anyLong(), any(Role.class));

        mockMvc.perform(patch("/user/{id}", mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).changeRole(mockUser.getId(), role);
    }

    @Test
    void updateUserShouldAddRoleWithoutPermissions() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);
        Role role = new Role("Admin");

        doNothing().when(userService).changeRole(anyLong(), any(Role.class));

        mockMvc.perform(patch("/user/{id}", mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isForbidden())
                .andDo(print());

    }

    @Test
    void updateUserShouldAddRoleNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Admin")));
        String token = jwtProvider.generateAccessToken(mockUser);
        Role role = new Role("Admin");

        doThrow(NotFoundException.class).when(userService).changeRole(anyLong(), any(Role.class));

        mockMvc.perform(patch("/user/{id}", mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    void updateUserShouldAddRoleBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Admin")));
        String token = jwtProvider.generateAccessToken(mockUser);
        Role role = new Role("Admin");

        doThrow(NotFoundException.class).when(userService).changeRole(anyLong(), any(Role.class));

        mockMvc.perform(patch("/user/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void getUserByLoginOrEmailShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        when(userService.findByEmailOrLogin(mockUser.getLogin())).thenReturn(mockUser);

        mockMvc.perform(get("/user/email-or-login")
                        .queryParam("email-or-login", mockUser.getLogin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).findByEmailOrLogin(mockUser.getLogin());
    }

    @Test
    void getUserByLoginOrEmailShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/user/email-or-login")
                        .queryParam("email-or-login", String.valueOf(1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void getUserByLoginOrEmailShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        doThrow(NotFoundException.class).when(userService).findByEmailOrLogin(mockUser.getLogin());

        mockMvc.perform(get("/user/email-or-login")
                        .queryParam("email-or-login", mockUser.getLogin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getUserByLoginOrEmailShouldReturnBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setEmail("");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        doThrow(NotFoundException.class).when(userService).findByEmailOrLogin(mockUser.getLogin());

        mockMvc.perform(get("/user/email-or-login")
                        .queryParam("email-or-login", mockUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
