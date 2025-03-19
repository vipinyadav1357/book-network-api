package com.vipin.book;

import com.vipin.book.model.Role;
import com.vipin.book.repo.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Runner {
    @Bean
    CommandLineRunner commandLineRunner(RoleRepo repo) {
        return args -> {
            if (repo.findByName("USER").isEmpty()) {
                repo.save(Role.builder().name("USER").build());
            }
        };
    }

}
