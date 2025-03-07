package com.jmd0.events.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth->
                auth 
                    .requestMatchers(
                        "/",
                        "/css/**",
                        "/js/**",
                        "/users/loginForm",
                        "/users/register",
                        "/api/users**"
                    ).permitAll()
                    .requestMatchers("/events**").hasRole("USER")
                    .anyRequest().authenticated()
            )
            .formLogin(loginForm ->
                loginForm
                    .loginPage("/users/loginForm")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/events", true)
                    .failureUrl("/users/loginForm?error=true")
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/index")
            )
            ;
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
    
}
