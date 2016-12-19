package ru.mipt.java2016.homework.g596.ivanova.task4;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.mipt.java2016.homework.g596.ivanova.task1.BestCalculatorEver;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = RestCalculatorApplication.class)
public class RestCalculatorApplication {
    @Bean
    public BestCalculatorEver calculator() {
        return new BestCalculatorEver();
    }

    @Bean
    public EmbeddedServletContainerCustomizer customizer(
            @Value("${ru.mipt.java2016.homework.g596.ivanova.task4.httpPort:9001}") int port) {
        return container -> container.setPort(port);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RestCalculatorApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}