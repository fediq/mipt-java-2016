package ru.mipt.java2016.homework.g596.kozlova.task4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BillingDatabaseConfiguration {
    @Bean
    public DataSource billingDataSource(
            @Value("${ru.mipt.java2016.homework.g596.kozlova.task4.jdbcUrl}") String jdbcUrlAdress,
            @Value("${ru.mipt.java2016.homework.g596.kozlova.task4.userName:}") String userName,
            @Value("${ru.mipt.java2016.homework.g596.kozlova.task4.password:}") String password
    ) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setJdbcUrl(jdbcUrlAdress);
        config.setUsername(userName);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}