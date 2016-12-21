package ru.mipt.java2016.homework.g597.vasilyev.task4;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.mipt.java2016.homework.g597.vasilyev.task1.ExtendableCalculator;
import ru.mipt.java2016.homework.g597.vasilyev.task1.ShuntingYardCalculator;

/**
 * Created by mizabrik on 21.12.16.
 */

@SpringBootApplication
public class RestfulCalculatorApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RestfulCalculatorApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    @Bean
    ExtendableCalculator calculator() {
        return new ShuntingYardCalculator();
    }
}
