package ru.mipt.java2016.homework.g597.bogdanov.task4.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by Semyo_000 on 20.12.2016.
 */
@Configuration
public class CalculatorDatabaseConfiguration {
    @Bean
    public DataSource billingDataSource(
            @Value("${ru.mipt.java2016.homework.g597.bogdanov.task4.server.jdbcUrl}") String jdbcUrl,
            @Value("${ru.mipt.java2016.homework.g597.bogdanov.task4.server.username:}") String username,
            @Value("${ru.mipt.java2016.homework.g597.bogdanov.task4.server.password:}") String password
    ) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
