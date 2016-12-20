package ru.mipt.java2016.homework.g596.litvinov.task4;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g596.litvinov.task1.MyCalculator;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 19.12.16.
 */


/**
 * curl http://localhost:9001/eval \
 * -X POST \
 * -H "Content-Type: text/plain" \
 * -H "Authorization: Basic $(echo -n "username:password" | base64)" \
 * --data-raw "44*3+2"
 */

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = App.class)
public class App {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    @Bean
    public Calculator calculator() {
        return new MyCalculator();
    }

    @Bean
    public EmbeddedServletContainerCustomizer customizer(
            @Value("${ru.mipt.java2016}.homework.g596.litvinov.task4.httpPort:9001}") int port) {
        return container -> container.setPort(port);
    }
}
