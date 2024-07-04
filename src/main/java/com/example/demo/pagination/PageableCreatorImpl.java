package com.example.demo.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableCreatorImpl implements PageableCreator {
    @Override
    public Pageable create(PaginationParams paginationParams) {
        return PageRequest.of(
                paginationParams.getPage(),
                paginationParams.getSize(),
                Sort.by(paginationParams.getSortBy())
        );
    }
}
