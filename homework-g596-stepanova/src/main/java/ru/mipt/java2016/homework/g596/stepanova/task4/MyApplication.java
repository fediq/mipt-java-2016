package ru.mipt.java2016.homework.g596.stepanova.task4;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g596.stepanova.task1.MyCalc;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = MyApplication.class)
public class MyApplication {
    @Bean
    public Calculator calculator() {
        return new MyCalc();
    }

    @Bean
    public EmbeddedServletContainerCustomizer customizer(
            @Value("${ru.mipt.java2016.homework.g596.stepanova.task4.httpPort:9001}") int port) {
        return container -> container.setPort(port);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MyApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}