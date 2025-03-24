package me.bsuir.easyattend.config;

import me.bsuir.easyattend.dto.get.ConfirmedUserDto;
import me.bsuir.easyattend.utils.InMemoryCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public InMemoryCache<String, List<ConfirmedUserDto>> confirmedUsersCache() {
        return new InMemoryCache<>();
    }

    // ... Other caches ...
}