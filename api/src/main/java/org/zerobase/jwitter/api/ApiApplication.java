package org.zerobase.jwitter.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.repository.UserRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"org.zerobase.jwitter.domain"})
@EntityScan(basePackages = {"org.zerobase.jwitter"})
@ComponentScan(basePackages = {"org.zerobase.jwitter"})
public class ApiApplication implements CommandLineRunner {
    private final UserRepository userRepository;

    public ApiApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User("gomawoe");
        userRepository.save(user);

    }
}
