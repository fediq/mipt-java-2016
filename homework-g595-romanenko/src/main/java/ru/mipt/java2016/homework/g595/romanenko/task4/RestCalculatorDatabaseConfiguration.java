package ru.mipt.java2016.homework.g595.romanenko.task4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@SuppressWarnings("Duplicates")
@Configuration
public class RestCalculatorDatabaseConfiguration {

    @Bean
    public DataSource restCalculatorDataSource(
            @Value("${ru.mipt.java2016.homework.g595.romanenko.task4.jdbc}") String jdbcUrl,
            @Value("${ru.mipt.java2016.homework.g595.romanenko.task4.username:}") String username,
            @Value("${ru.mipt.java2016.homework.g595.romanenko.task4.password:}") String password
    ) {
        //String jdbcUrl = "jdbc:h2:~/task4.db;AUTO_SERVER=TRUE";
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setJdbcUrl("jdbc:h2:" + jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
