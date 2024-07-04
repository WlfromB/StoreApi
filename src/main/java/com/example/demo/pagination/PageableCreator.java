package com.example.demo.pagination;

import org.springframework.data.domain.Pageable;

@FunctionalInterface
public interface PageableCreator {
    Pageable create(PaginationParams paginationParams);
}
