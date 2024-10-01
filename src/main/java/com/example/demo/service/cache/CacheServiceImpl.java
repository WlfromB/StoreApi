package com.example.demo.service.cache;

import com.example.demo.constant.classes.CacheName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {        
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    @Autowired
    public CacheServiceImpl(ObjectMapper objectMapper, @Qualifier(CacheName.CACHE) JedisPool jedisPool) {
        this.objectMapper = objectMapper;
        this.jedisPool = jedisPool;
    }
    
    @Override
    public <T> T getFromCache(String key, TypeReference<T> typeReference) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json != null) {
                return objectMapper.readValue(json, typeReference);
            }
        }
        return null;
    }

    @Override
    public <T> void setToCache(String key, T value, int ttl) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = objectMapper.writeValueAsString(value);
            jedis.setex(key, ttl, json);
        }
    }
}
