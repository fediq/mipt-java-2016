package ru.mipt.java2016.homework.g595.romanenko.task4;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4.base
 *
 * @author Ilya I. Romanenko
 * @since 12.12.16
 **/
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        super.configurePathMatch(configurer);
        configurer.setUseSuffixPatternMatch(true);
    }
}