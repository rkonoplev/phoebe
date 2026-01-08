package com.example.phoebe.integration.config;

import com.example.phoebe.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

class CacheConfigTest extends BaseIntegrationTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void cacheManagerShouldBeConfigured() {
        assertThat(cacheManager).isNotNull();
    }

    @Test
    void cacheManagerShouldCreateCaches() {
        assertThat(cacheManager.getCache("news-by-id")).isNotNull();
        assertThat(cacheManager.getCache("news-by-term")).isNotNull();
    }

    @Test
    void cacheShouldStoreAndRetrieveValues() {
        Cache cache = cacheManager.getCache("news-by-id");
        assertThat(cache).isNotNull();

        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);
        String retrievedValue = cache.get(key, String.class);

        assertThat(retrievedValue).isEqualTo(value);
    }
}