package com.github.plantfern.foodDiary.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig{

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // для тестирования в Postman
            .authorizeHttpRequests(auth -> auth
                    // .requestMatchers("/**").permitAll() // для работы с Postman

                    .requestMatchers("/login", "/register").permitAll()

                    .requestMatchers("/diary_profile/**").hasRole("DIARY_PROFILE")
                    .requestMatchers("/observer/**").hasAnyRole("OBSERVER", "SPECIALIST", "ADMINISTRATOR")
                    .requestMatchers("/specialist/**").hasRole("SPECIALIST")

                    .requestMatchers("/moderator/**").hasAnyRole("MODERATOR", "ADMINISTRATOR")
                    .requestMatchers("/admin/**").hasRole("ADMINISTRATOR")
                    .anyRequest().authenticated()
            )
            // для Postman и SPA
            .httpBasic(Customizer.withDefaults()) // Пока что Basic auth
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
