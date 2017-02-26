package ru.mipt.java2016.homework.g595.novikov.task4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by igor on 11/28/16.
 */
@SpringBootApplication
public class MyServer {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MyServer.class, args);
    }
}
