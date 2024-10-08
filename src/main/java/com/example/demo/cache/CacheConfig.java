package com.example.demo.cache;

import com.example.demo.constant.classes.CacheName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class CacheConfig {
    @Value("${uri.redis}")
    private String hostMethodsCache;

    @Value("${uri.codes}")
    private String hostActivationCodesCache;
    
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMinIdle(1);
        jedisPoolConfig.setMaxWaitMillis(2000);
        jedisPoolConfig.setJmxEnabled(false);
        return jedisPoolConfig;
    }

    @Bean(name = CacheName.CACHE)
    public JedisPool cacheJedisPool(JedisPoolConfig jedisPoolConfig) {
        return new JedisPool(jedisPoolConfig, hostMethodsCache);
    }

    @Bean(name = CacheName.VERIFICATION)
    public JedisPool verificationJedisPool(JedisPoolConfig jedisPoolConfig) {
        return new JedisPool(jedisPoolConfig, hostActivationCodesCache);
    }

}