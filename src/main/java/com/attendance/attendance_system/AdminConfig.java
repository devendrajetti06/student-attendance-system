package com.attendance.attendance_system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
public class AdminConfig {

    @Bean
    CommandLineRunner createAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (adminRepository.findByUsername("admin").isEmpty()) {

                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(
passwordEncoder.encode(
    System.getenv("ADMIN_PASSWORD")
)                );

                adminRepository.save(admin);

                System.out.println("Admin account created successfully!");
            }
        };
    }
}