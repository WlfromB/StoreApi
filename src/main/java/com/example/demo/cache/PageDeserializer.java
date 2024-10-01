package com.example.demo.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class PageDeserializer<T> extends PageImpl<T> {
    private static final String CONTENT_PARAM = "content";
    private static final String PAGE_PARAM = "number";
    private static final String PAGE_SIZE_PARAM = "size";
    private static final String PAGE_TOTAL_ELEMENTS_PARAM = "totalElements";

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PageDeserializer(@JsonProperty(CONTENT_PARAM) List<T> content,
                            @JsonProperty(PAGE_PARAM) int page,
                            @JsonProperty(PAGE_SIZE_PARAM) int size,
                            @JsonProperty(PAGE_TOTAL_ELEMENTS_PARAM) long total
    ) {
        super(content, PageRequest.of(page, size), total);
    }

    public PageDeserializer(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }
}

