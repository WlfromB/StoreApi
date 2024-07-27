package com.example.demo.controllers;

import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Book;
import com.example.demo.entities.Discount;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.auth.JwtProvider;
import com.example.demo.service.book.BookService;
import com.example.demo.service.discount.DiscountService;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DiscountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PageableCreator pageableCreator;

    @BeforeEach
    void setUp() {}
    
    
    @Test
    void createDiscountWithoutRolesShouldReturnForbidden() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("login");
        mockUser.setPassword("password");
        mockUser.setEmail("email@email.com");
        mockUser.setRoles(Set.of(new Role("Customer")));
        
        String token = jwtProvider.generateAccessToken(mockUser);
        
        mockMvc.perform(post("/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    
    @Test
    void createDiscountWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/discount")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    
    @Test
    void createDiscountWithValidRolesShouldReturnCreated() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("login");
        mockUser.setPassword("password");
        mockUser.setEmail("email@email.com");
        mockUser.setRoles(Set.of(new Role("Customer"), new Role("Admin")));

        String token = jwtProvider.generateAccessToken(mockUser);

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.MAX);
        discountDto.setDateOfSaleStart(LocalDate.now());
        discountDto.setDimensionOfSale(BigDecimal.valueOf(20.0));

        Book mockBook = new Book();
        
        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);
        
        when(discountService.saveDiscount(discountDto)).thenReturn(discount);
        
        mockMvc.perform(post("/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(discountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(discount.getId()));
        
        verify(discountService,times(1)).saveDiscount(discountDto);
    }

    @Test
    void createDiscountWithValidRolesShouldReturnNotFound() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("login");
        mockUser.setPassword("password");
        mockUser.setEmail("email@email.com");
        mockUser.setRoles(Set.of(new Role("Customer"), new Role("Admin")));

        String token = jwtProvider.generateAccessToken(mockUser);

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.MAX);
        discountDto.setDateOfSaleStart(LocalDate.now());
        discountDto.setDimensionOfSale(BigDecimal.valueOf(20.0));

        Book mockBook = new Book();

        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);

        when(discountService.saveDiscount(discountDto)).thenThrow(NotFoundException.class);

        mockMvc.perform(post("/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(discountDto)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(discountService,times(1)).saveDiscount(discountDto);
    }

    @Test
    void createDiscountWithInvalidDtoShouldReturnBadRequest() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("login");
        mockUser.setPassword("password");
        mockUser.setEmail("email@email.com");
        mockUser.setRoles(Set.of(new Role("Customer"), new Role("Admin")));

        String token = jwtProvider.generateAccessToken(mockUser);

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.of(2000,1, 2));
        discountDto.setDateOfSaleStart(LocalDate.of(2000,1, 1));
        discountDto.setDimensionOfSale(BigDecimal.valueOf(-1));

        Book mockBook = new Book();

        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);

        when(discountService.saveDiscount(discountDto)).thenReturn(discount);

        mockMvc.perform(post("/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(discountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDiscountByAuthorIdWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.of(2000,1, 2));
        discountDto.setDateOfSaleStart(LocalDate.of(2000,1, 1));
        discountDto.setDimensionOfSale(BigDecimal.valueOf(-1));

        Book mockBook = new Book();

        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);

        Page<Discount> discountPage = new PageImpl<>(Collections.singletonList(discount), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(discountService.getDiscountByAuthor(anyLong(), any(Pageable.class))).thenReturn(discountPage);

        mockMvc.perform(get("/discount/author")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());

        verify(discountService, times(1)).getDiscountByAuthor(anyLong(), any(Pageable.class));
    }

    @Test
    void getDiscountByAuthorIdWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/discount/author")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getDiscountByAuthorIdWithTokenShouldReturnNotFound() throws Exception {
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
        doThrow(NotFoundException.class).when(discountService).getDiscountByAuthor(anyLong(), any(Pageable.class));

        mockMvc.perform(get("/discount/author")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getDiscountByDateWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.of(2000,1, 2));
        discountDto.setDateOfSaleStart(LocalDate.of(2000,1, 1));
        discountDto.setDimensionOfSale(BigDecimal.valueOf(-1));

        Book mockBook = new Book();

        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);

        Page<Discount> discountPage = new PageImpl<>(Collections.singletonList(discount), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(discountService.getDiscountsByDate(any(LocalDate.class), any(Pageable.class))).thenReturn(discountPage);

        mockMvc.perform(get("/discount/date")
                        .param("date", "2024-07-18")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());

        verify(discountService, times(1)).getDiscountsByDate(any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void getDiscountByDateWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/discount/date")
                        .param("date", "2024-07-18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getDiscountByDateWithTokenShouldReturnNotFound() throws Exception {
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
        doThrow(NotFoundException.class).when(discountService).getDiscountsByDate(any(LocalDate.class), any(Pageable.class));

        mockMvc.perform(get("/discount/date")
                        .param("date", "2024-07-18")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getDiscountsByBookAndDateWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.of(2000,1, 2));
        discountDto.setDateOfSaleStart(LocalDate.of(2000,1, 1));
        discountDto.setDimensionOfSale(BigDecimal.valueOf(-1));

        Book mockBook = new Book();

        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);

        Page<Discount> discountPage = new PageImpl<>(Collections.singletonList(discount), pageable, 1);
        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(discountService.getDiscountByBookAndDate(anyLong(), any(LocalDate.class), any(Pageable.class))).thenReturn(discountPage);

        mockMvc.perform(get("/discount")
                        .param("bookId", "1")
                        .param("date", "2024-07-18")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());

        verify(discountService, times(1)).getDiscountByBookAndDate(anyLong(), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void getDiscountsByBookAndDateWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/discount")
                        .param("bookId", "1")
                        .param("date", "2024-07-18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getDiscountsByBookAndDateWithTokenShouldReturnNotFound() throws Exception {
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
        doThrow(NotFoundException.class).when(discountService).getDiscountByBookAndDate(anyLong(), any(LocalDate.class), any(Pageable.class));

        mockMvc.perform(get("/discount")
                        .param("bookId", "1")
                        .param("date", "2024-07-18")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getDiscountByBookWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.of(2000,1, 2));
        discountDto.setDateOfSaleStart(LocalDate.of(2000,1, 1));
        discountDto.setDimensionOfSale(BigDecimal.valueOf(-1));

        Book mockBook = new Book();

        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);

        Page<Discount> discountPage = new PageImpl<>(Collections.singletonList(discount), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(discountService.getDiscountsByBook(anyLong(), any(Pageable.class))).thenReturn(discountPage);

        mockMvc.perform(get("/discount/book")
                        .param("bookId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());

        verify(discountService, times(1)).getDiscountsByBook(anyLong(), any(Pageable.class));
    }

    @Test
    void getDiscountByBookWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/discount/book")
                        .param("bookId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getDiscountByBookWithTokenShouldReturnNotFound() throws Exception {
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
        doThrow(NotFoundException.class).when(discountService).getDiscountsByBook(anyLong(), any(Pageable.class));

        mockMvc.perform(get("/discount/book")
                        .param("bookId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDiscountsWithTokenShouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setLogin("testUser");
        mockUser.setRoles(Set.of(new Role("Customer")));
        String token = jwtProvider.generateAccessToken(mockUser);

        PaginationParams paginationParams = new PaginationParams();
        paginationParams.setSize(10);
        paginationParams.setPage(0);
        paginationParams.setSortBy("id");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        DiscountDto discountDto = new DiscountDto();
        discountDto.setBookId(1L);
        discountDto.setDateOfSaleEnd(LocalDate.of(2000,1, 2));
        discountDto.setDateOfSaleStart(LocalDate.of(2000,1, 1));
        discountDto.setDimensionOfSale(BigDecimal.valueOf(-1));

        Book mockBook = new Book();

        Discount discount = discountDto.from(mockBook);
        discount.setId(1L);

        Page<Discount> discountPage = new PageImpl<>(Collections.singletonList(discount), pageable, 1);

        when(pageableCreator.create(paginationParams)).thenReturn(pageable);
        when(discountService.getAll(any(Pageable.class))).thenReturn(discountPage);

        mockMvc.perform(get("/discount/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());

        verify(discountService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    void getAllDiscountsWithoutTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/discount/discounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllDiscountsWithTokenShouldReturnNotFound() throws Exception {
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
        doThrow(NotFoundException.class).when(discountService).getAll(any(Pageable.class));

        mockMvc.perform(get("/discount/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
