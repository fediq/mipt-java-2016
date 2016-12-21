package ru.mipt.java2016.homework.g597.vasilyev.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

/**
 * Created by mizabrik on 21.12.16.
 */
@Configuration
@EnableWebSecurity
public class SecurityServiceConfguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private CalculatorDao calculatorDao;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().realmName("Calculator").and()
                .csrf().disable()
                .authorizeRequests()
                //.antMatchers("/eval/").authenticated()
                .antMatchers("/variable/**").authenticated()
                .antMatchers("/function/**").authenticated()
                .anyRequest().permitAll();
    }

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> {
            try {
                CalculatorUser user = calculatorDao.loadUser(username);
                return new User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singletonList(() -> "AUTH")
                );
            } catch (EmptyResultDataAccessException e) {
                throw new UsernameNotFoundException(username);
            }
        });
    }
}
