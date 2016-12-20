package ru.mipt.java2016.homework.g595.proskurin.task4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Александр on 17.12.2016.
 */

@Configuration
@SpringBootApplication
public class Service extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Service.class, args);
    }
}
