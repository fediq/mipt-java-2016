package ru.mipt.java2016.homework.g595.belyh.task4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * Created by white2302 on 17.12.2016.
 */

@Configuration
@SpringBootApplication
public class CalculatorService extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(CalculatorService.class, args);
    }
}
