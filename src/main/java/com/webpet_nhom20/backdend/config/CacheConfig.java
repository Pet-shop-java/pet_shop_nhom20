package com.webpet_nhom20.backdend.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(List.of(
                buildCache("product_list", 5),
                buildCache("product_detail", 10),
                buildCache("categories", 60)
        ));

        return cacheManager;
    }

    private CaffeineCache buildCache(String name, int minutes) {
        return new CaffeineCache(
                name,
                Caffeine.newBuilder()
                        .expireAfterWrite(minutes, TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .build()
        );
    }
}

