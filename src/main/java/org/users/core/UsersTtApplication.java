package org.users.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UsersTtApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersTtApplication.class, args);
    }

}
