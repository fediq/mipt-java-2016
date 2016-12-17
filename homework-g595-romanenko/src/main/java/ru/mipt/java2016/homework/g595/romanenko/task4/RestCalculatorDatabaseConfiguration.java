package ru.mipt.java2016.homework.g595.romanenko.task4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@SuppressWarnings("Duplicates")
@Configuration
public class RestCalculatorDatabaseConfiguration {

    @Bean
    public DataSource restCalculatorDataSource() {
        String jdbcUrl = "jdbc:h2:/run/media/ilya/LocalDriveD/Projects/" +
            "Java/mipt-java-2016/homework-g595-romanenko/RestCalculator;AUTO_SERVER=TRUE";
        String username = "";
        String password = "";
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
