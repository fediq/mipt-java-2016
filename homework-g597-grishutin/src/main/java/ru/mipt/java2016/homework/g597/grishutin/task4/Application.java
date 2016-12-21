package ru.mipt.java2016.homework.g597.grishutin.task4;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g597.grishutin.task1.MyCalculator;

/**
 * curl http://localhost:8080/eval \
 *     -X POST \
 *     -H "Content-Type: text/plain" \
 *     -H "Authorization: Basic $(echo -n "sashka:sashka" | base64)" \
 *     --data-raw "44*3+2"
 */

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = Application.class)
public class Application {

    @Bean
    public static Calculator calculator() {
        return MyCalculator.INSTANCE;
    }

    @Bean
    public EmbeddedServletContainerCustomizer customizer(
            @Value("${ru.mipt.java2016.homework.g597.grishutin.task4.httpPort:8080}") int port) {
        return container -> container.setPort(port);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
