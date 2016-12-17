package ru.mipt.java2016.homework.g594.borodin.task4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BillingDatabaseConfiguration {
    @Bean
    public DataSource billingDataSource(
            //@Value("${ru.mipt.java2016.homework.g000.lavrentyev.task4.jdbcUrl}") String jdbcUrl
            //@Value("${ru.mipt.java2016.homework.g000.lavrentyev.task4.username:}") String username,
            //@Value("${ru.mipt.java2016.homework.g000.lavrentyev.task4.password:}") String password
    ) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.h2.Driver.class.getName());
        config.setJdbcUrl("jdbc:h2:C:\\Users\\Maxim\\IdeaProjects\\mipt-java-2016-4\\mipt-java-2016\\homework-g594-borodin\\Database.mv.db");
        //config.setJdbcUrl(jdbcUrl);
        config.setUsername("");
        config.setPassword("");
        return new HikariDataSource(config);
    }
}
