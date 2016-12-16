package ru.mipt.java2016.homework.g594.rubanenko.task4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by king on 02.12.16.
 */
@Configuration
public class DatabaseConfiguration {
    @Bean
    public DataSource billingDataSource(
            @Value("${ru.mipt.java2016.homework.g594.rubanenko.task4.jdbcUrl:jdbc:h2:~/test}") String jdbcUrl,
            @Value("${ru.mipt.java2016.homework.g594.rubanenko.task4.username:}") String username,
            @Value("${ru.mipt.java2016.homework.g594.rubanenko.task4.password:}") String password
    ) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
