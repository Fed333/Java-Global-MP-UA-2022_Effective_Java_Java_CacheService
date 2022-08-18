package org.fed333.example.cache.java.app.cache.impl.configuration;


import org.fed333.example.cache.java.app.cache.CustomCache;
import org.fed333.example.cache.java.app.cache.impl.JavaLFUCache;
import org.fed333.example.cache.java.app.entity.CacheValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestConfig {

    @Bean
    @Scope("prototype")
    @Primary
    public CustomCache<String, CacheValue> testCache() {
        return new JavaLFUCache();
    }


}
