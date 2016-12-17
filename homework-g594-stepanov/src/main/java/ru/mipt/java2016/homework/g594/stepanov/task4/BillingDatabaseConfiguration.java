package ru.mipt.java2016.homework.g594.stepanov.task4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingDatabaseConfiguration {
    @Bean
    public HikariDataSource billingDataSource(
            @Value("${ru.mipt.java2016.homework.g594.stepanov.task4.jdbcUrl}") String jdbcUrl,
            @Value("${ru.mipt.java2016.homework.g594.stepanov.task4.username:}") String username,
            @Value("${ru.mipt.java2016.homework.g594.stepanov.task4.password:}") String password
    ) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        HikariDataSource hikariDataSource = new HikariDataSource(config);

        return hikariDataSource;
    }
}
