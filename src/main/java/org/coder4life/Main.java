package org.coder4life;

import org.coder4life.domain.RegistrationDto;
import org.coder4life.domain.Role;
import org.coder4life.service.RoleService;
import org.coder4life.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService, RoleService roleService) {
        return args -> {
            Role roleAdmin = roleService.save(new Role(Role.ROLE_ADMIN));
            Role roleUser = roleService.save(new Role(Role.ROLE_USER));

            List<Role> roles1 = new ArrayList<>();
            roles1.add(roleAdmin);

            List<Role> roles2 = new ArrayList<>();
            roles1.add(roleUser);

            userService.create(new RegistrationDto("John Doe", "johndoe", "1234"), roles1);
            userService.create(new RegistrationDto("Jane Doe", "janedoe", "1234"), roles2);
        };
    }

}
