package com.libreria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para API REST
            .authorizeHttpRequests(auth -> auth
                // Endpoints publicos de la API
                .requestMatchers("/api/libros/**", "/api/auth/**", "/api/categories/**",
                                "/api/autores/**", "/api/editoriales/**", "/api/test/**",
                                "/api/chat/**", "/auth/**").permitAll()
                // Endpoints de administración
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());
        
        return http.build();
    }
}