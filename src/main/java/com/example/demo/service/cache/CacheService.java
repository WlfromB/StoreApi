package com.example.demo.service.cache;

import com.fasterxml.jackson.core.type.TypeReference;

public interface CacheService {
    <T> T getFromCache(String key, TypeReference<T> typeReference) throws Exception;
    <T> void setToCache(String key, T value, int ttl) throws Exception;
}
