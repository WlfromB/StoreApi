package com.example.demo.controllers;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.auth.JwtProvider;
import com.example.demo.service.author.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private PageableCreator pageableCreator;


    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAuthorsWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/author/authors")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAuthorsWithValidTokenShouldReturnAuthors() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Oleg");
        author.setLastName("Smirnov");
        author.setDateOfBirthday(LocalDate.of(1970, 12, 20));
        author.setBooks(Collections.emptySet());

        Page<Author> authorPage = new PageImpl<>(Collections.singletonList(author), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(authorService.getAllAuthors(any(Pageable.class))).thenReturn(authorPage);

        mockMvc.perform(get("/author/authors")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(author.getId()));

        verify(authorService).getAllAuthors(any(Pageable.class));
    }

    @Test
    void getAuthorByIdWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/author/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void getAuthorByIdWithValidTokenShouldReturnAuthor() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        Author mockAuthor = new Author();
        mockAuthor.setId(1L);
        mockAuthor.setFirstName("Oleg");
        mockAuthor.setLastName("Smirnov");
        mockAuthor.setDateOfBirthday(LocalDate.of(1970, 12, 20));
        mockAuthor.setBooks(Collections.emptySet());

        when(authorService.getAuthorById(1L)).thenReturn(mockAuthor);

        mockMvc.perform(get("/author")
                        .param("id", "1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockAuthor.getId()))
                .andReturn();

        verify(authorService, times(1)).getAuthorById(1L);
    }

    @Test
    void patchInsertBookToAuthorShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/author/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchInsertBookToAuthorWithoutRoleShouldReturnForbidden() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto mockBookDto = new BookDto();
        mockBookDto.setTitle("title");
        mockBookDto.setDateCreated(LocalDate.of(2020, 1, 1));
        mockBookDto.setPrice(BigDecimal.valueOf(200.0));
        mockBookDto.setDescription("description");
        mockBookDto.setNumberPages(100);

        mockMvc.perform(patch("/author/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockBookDto))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    void patchInsertBookToAuthorWithRoleShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Admin"))); // Исправлено на роль Admin
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto mockBookDto = new BookDto();
        mockBookDto.setTitle("title");
        mockBookDto.setDateCreated(LocalDate.of(2020, 1, 1));
        mockBookDto.setPrice(BigDecimal.valueOf(200.0));
        mockBookDto.setDescription("description");
        mockBookDto.setNumberPages(100);

        mockMvc.perform(patch("/author/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockBookDto))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void patchInsertBookToAuthorWithValidTokenShouldReturnString() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer"), new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto mockBookDto = new BookDto();
        mockBookDto.setTitle("title");
        mockBookDto.setDateCreated(LocalDate.of(2020, 1, 1));
        mockBookDto.setPrice(BigDecimal.valueOf(200.0));
        mockBookDto.setDescription("description");
        mockBookDto.setNumberPages(100);

        mockMvc.perform(patch("/author/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockBookDto)))
                .andExpect(status().isOk());

        verify(authorService, times(1)).addBook(mockBookDto, 1L);
    }
}