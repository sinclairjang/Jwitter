package org.zerobase.jwitter.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"org.zerobase.jwitter.domain"})
@EnableRedisRepositories(basePackages = {"org.zerobase.jwitter.domain.repository.cache"},
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@EntityScan(basePackages = {"org.zerobase.jwitter"})
@ComponentScan(basePackages = {"org.zerobase.jwitter"})
@SpringBootApplication
public class ApiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
