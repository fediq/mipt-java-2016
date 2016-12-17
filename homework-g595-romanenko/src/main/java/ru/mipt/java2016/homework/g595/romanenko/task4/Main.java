package ru.mipt.java2016.homework.g595.romanenko.task4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 11.12.16
 **/

@SpringBootApplication
@Configuration
public class Main extends SpringBootServletInitializer {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    /*
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Main.class);
        //application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }*/
}