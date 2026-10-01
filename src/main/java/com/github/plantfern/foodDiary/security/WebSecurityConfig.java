package com.github.plantfern.foodDiary.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig{

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable) // для тестирования в Postman
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/registration").permitAll()
                    .requestMatchers("/api/users/my-profile", "/api/diary/profile/onboarding/**").authenticated()

                    .requestMatchers("/api/diary-profile/**").hasRole("DIARY_PROFILE")
                    .requestMatchers("/api/specialist/**").hasAnyRole("SPECIALIST", "MODERATOR", "ADMINISTRATOR")

                    .requestMatchers("/moderator/**").hasAnyRole("MODERATOR", "ADMINISTRATOR")
                    .requestMatchers("/admin/**").hasRole("ADMINISTRATOR")
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()) // Пока что Basic auth
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
					response.getWriter().write("""
						{"status": 401, "error": "Unauthorized", "message": "Authentication required"}
					""");
				}));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
