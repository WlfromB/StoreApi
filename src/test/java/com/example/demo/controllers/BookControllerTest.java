package com.example.demo.controllers;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.auth.JwtProvider;
import com.example.demo.service.book.BookService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PageableCreator pageableCreator;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllBooksWithoutTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        BookDto mockBookDto = new BookDto();
        mockBookDto.setTitle("title");
        mockBookDto.setDateCreated(LocalDate.of(2020, 1, 1));
        mockBookDto.setPrice(BigDecimal.valueOf(200.0));
        mockBookDto.setDescription("description");
        mockBookDto.setNumberPages(100);

        Book mockBook = mockBookDto.from();

        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(mockBook), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/book/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooks(any(Pageable.class));
    }

    @Test
    void getAllBooksWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/book/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllBooksWithoutTokenShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        BookDto mockBookDto = new BookDto();
        mockBookDto.setTitle("title");
        mockBookDto.setDateCreated(LocalDate.of(2020, 1, 1));
        mockBookDto.setPrice(BigDecimal.valueOf(200.0));
        mockBookDto.setDescription("description");
        mockBookDto.setNumberPages(100);

        Book mockBook = mockBookDto.from();

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        doThrow(NotFoundException.class).when(bookService).getAllBooks(any(Pageable.class));

        mockMvc.perform(get("/book/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBookWithTokenShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);
        doThrow(NotFoundException.class).when(bookService).getBookById(anyLong());
        mockMvc.perform(get("/book")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("id", String.valueOf(anyLong()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(anyLong());
    }

    @Test
    void getBookWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto mockBookDto = new BookDto();
        mockBookDto.setTitle("title");
        mockBookDto.setDateCreated(LocalDate.of(2020, 1, 1));
        mockBookDto.setPrice(BigDecimal.valueOf(200.0));
        mockBookDto.setDescription("description");
        mockBookDto.setNumberPages(100);

        Book mockBook = mockBookDto.from();
        mockBook.setId(1L);

        when(bookService.getBookById(mockUser.getId())).thenReturn(mockBook);

        mockMvc.perform(get("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("id", String.valueOf(mockBook.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBook.getId()));

        verify(bookService, times(1)).getBookById(mockBook.getId());
    }

    @Test
    void getBooksByAuthorsWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        BookDto mockBookDto = new BookDto();
        mockBookDto.setTitle("title");
        mockBookDto.setDateCreated(LocalDate.of(2020, 1, 1));
        mockBookDto.setPrice(BigDecimal.valueOf(200.0));
        mockBookDto.setDescription("description");
        mockBookDto.setNumberPages(100);

        Book mockBook = mockBookDto.from();

        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(mockBook), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(bookService.getBooksByAuthors(anyList(), any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/book/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("authorId", "1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookService, times(1)).getBooksByAuthors(anyList(), any(Pageable.class));
    }

    @Test
    void getBooksByAuthorsWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/book/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("authorId", "1"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getBooksByAuthorsWithInvalidAuthorsShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);
        List<Long> authorIds = List.of(-1L);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        doThrow(new NotFoundException("No books found")).when(bookService).getBooksByAuthors(authorIds, pageable);

        mockMvc.perform(get("/book/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .queryParam("authorId", "-1")
                        .queryParam("size", "10")
                        .queryParam("page", "0")
                        .queryParam("sortBy", "id"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBooksByAuthors(authorIds, pageable);
    }

    @Test
    void createBookInvalidDtoShouldReturnBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto bookDto = new BookDto();
        mockMvc.perform(post("/book")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void createBookBlankTitleShouldReturnBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("");
        bookDto.setDescription("Some description");
        bookDto.setNumberPages(100);
        bookDto.setDateCreated(LocalDate.now());
        bookDto.setPrice(new BigDecimal("19.99"));
        mockMvc.perform(post("/book")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void createBookInvalidPriceShouldReturnBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("Valid Title");
        bookDto.setDescription("Some description");
        bookDto.setNumberPages(100);
        bookDto.setDateCreated(LocalDate.now());
        bookDto.setPrice(new BigDecimal("0.00"));
        mockMvc.perform(post("/book")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void createBookValidDtoShouldReturnCreated() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("Valid Title");
        bookDto.setDescription("Some description");
        bookDto.setNumberPages(100);
        bookDto.setDateCreated(LocalDate.now());
        bookDto.setPrice(new BigDecimal("19.99"));

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Valid Title");
        book.setDescription("Some description");
        book.setNumberPages(100);
        book.setDateCreated(LocalDate.now());
        book.setPrice(new BigDecimal("19.99"));

        when(bookService.saveBook(bookDto)).thenReturn(book);

        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/book/1"))
                .andDo(print());

        verify(bookService, times(1)).saveBook(bookDto);
    }

    @Test
    void createBookValidDtoWithoutPermissionShouldReturnForbidden() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void updateBookValidAuthorsShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        long bookId = 1L;
        List<Long> authorIds = Arrays.asList(2L, 3L);

        Book updatedBook = new Book();
        updatedBook.setId(bookId);

        when(bookService.setAuthors(bookId, authorIds)).thenReturn(updatedBook);

        mockMvc.perform(patch("/book/{id}/authors", bookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorIds)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookService, times(1)).setAuthors(bookId, authorIds);
    }

    @Test
    void updateBookInvalidIdShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        long invalidBookId = 999L;
        List<Long> authorIds = Arrays.asList(2L, 3L);

        when(bookService.setAuthors(invalidBookId, authorIds)).thenThrow(new NotFoundException("Book not found"));

        mockMvc.perform(patch("/book/{id}/authors", invalidBookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorIds)))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(bookService, times(1)).setAuthors(invalidBookId, authorIds);
    }

    @Test
    void updateBookEmptyAuthorListShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Author")));
        String token = jwtProvider.generateAccessToken(mockUser);

        long bookId = 1L;
        List<Long> authorIds = Collections.emptyList();

        Book updatedBook = new Book();
        updatedBook.setId(bookId);

        when(bookService.setAuthors(bookId, authorIds)).thenReturn(updatedBook);

        mockMvc.perform(patch("/book/{id}/authors", bookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorIds)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookService, times(1)).setAuthors(bookId, authorIds);
    }

    @Test
    void updateBookWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/book/{id}/authors", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateBookWithoutPermissionsShouldReturnForbidden() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        mockMvc.perform(patch("/book/{id}/authors", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
