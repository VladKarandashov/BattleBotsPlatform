package ru.abradox.middlewareservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.abradox")
public class MiddlewareServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiddlewareServiceApplication.class, args);
    }

}
