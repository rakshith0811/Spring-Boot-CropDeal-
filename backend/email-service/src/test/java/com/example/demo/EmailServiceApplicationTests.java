package com.example.demo; // Keep this package if you want, but fix import and annotation

import com.cropdeal.mail.EmailServiceApplication; // Import your actual main application class
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan; // Optional: If you have other components in 'com.example.demo'

@SpringBootTest(classes = EmailServiceApplication.class) // Explicitly tell Spring Boot where your main app is
// If your test needs to scan components in its own package (com.example.demo), you might need:
// @ComponentScan(basePackages = "com.example.demo")
class EmailServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test simply checks if the Spring application context loads successfully.
        // If it loads, it means your basic configuration and component scanning are working.
    }

}
