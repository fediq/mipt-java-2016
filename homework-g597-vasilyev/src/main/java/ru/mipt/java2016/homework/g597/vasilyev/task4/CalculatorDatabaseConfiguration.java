package ru.mipt.java2016.homework.g597.vasilyev.task4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by mizabrik on 21.12.16.
 */
@Configuration
public class CalculatorDatabaseConfiguration {
    @Bean
    public DataSource calculatorDataSource(
            @Value("${ru.mipt.java2016.homework.g597.mizabrik.task4.dbUrl}") String dbUrl,
            @Value("${ru.mipt.java2016.homework.g597.mizabrik.task4.dbUsername:}") String dbUsername,
            @Value("${ru.mipt.java2016.homework.g597.mizabrik.task4.dbPassword:}") String dbPassword
    ) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        return new HikariDataSource(config);
    }
}
