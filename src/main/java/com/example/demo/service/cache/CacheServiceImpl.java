package com.example.demo.service.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

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
  /* public <T> T getFromCache(String key, TypeReference<T> typeReference) {
       try (Jedis jedis = jedisPool.getResource()) {
           String data = jedis.get(key);
           if (data != null) {
               log.info("Data found in cache: " + data); // Отладочное сообщение
               return objectMapper.readValue(data, typeReference);
           } else {
               log.info("No data found in cache for key: " + key); // Отладочное сообщение
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }

    public void setToCache(String key, Object value, int ttl) {
        try (Jedis jedis = jedisPool.getResource()) {
            String data = objectMapper.writeValueAsString(value);
            System.out.println("Saving data to cache: " + data); // Отладочное сообщение
            jedis.setex(key, ttl, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
