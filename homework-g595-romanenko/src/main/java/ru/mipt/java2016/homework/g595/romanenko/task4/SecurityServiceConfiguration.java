package ru.mipt.java2016.homework.g595.romanenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 16.12.16
 **/
@Configuration
@EnableWebSecurity
public class SecurityServiceConfiguration extends WebSecurityConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceConfiguration.class);

    @Autowired
    private RestCalculatorDao restCalculatorDao;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOG.info("Configuring security");
        http
            .authorizeRequests()
            .antMatchers("/static_resources/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .formLogin()
            .loginPage("/login")
            .permitAll().defaultSuccessUrl("/load", true)
            .and()
            .logout().permitAll();
    }

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        LOG.info("Registering global user details service");
        auth.userDetailsService(username -> {
            try {
                RestUser user = restCalculatorDao.loadUser(username);
                return new User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(() -> "AUTH")
                );
            } catch (EmptyResultDataAccessException e) {
                LOG.warn("No such user: " + username);
                throw new UsernameNotFoundException(username);
            }
        });
    }
}
