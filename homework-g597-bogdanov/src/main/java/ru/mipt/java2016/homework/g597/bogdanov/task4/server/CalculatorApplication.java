package ru.mipt.java2016.homework.g597.bogdanov.task4.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.RESTCalculator;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.IFunctionalCalculator;

/**
 * Created by Semyo_000 on 20.12.2016.
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = CalculatorApplication.class)
public class CalculatorApplication {

    @Bean
    public IFunctionalCalculator calculator() {
        return new RESTCalculator();
    }

    @Bean
    public EmbeddedServletContainerCustomizer customizer(
            @Value("${ru.mipt.java2016.homework.g597.bogdanov.task4.server.httpPort:9001}") int port) {
        return container -> container.setPort(port);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CalculatorApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
