package com.libreria.config;

import com.libreria.JWT.JWTAuthenticationfilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTAuthenticationfilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para API REST
            .authorizeHttpRequests(auth -> auth
                // Endpoints publicos de la API
                .requestMatchers("/api/libros/**", "/api/auth/**", "/api/categories/**",
                                "/api/autores/**", "/api/editoriales/**", "/api/test/**",
                                "/api/chat/**", "/auth/**").permitAll()
                // Endpoints de usuarios - requieren autenticación
                .requestMatchers("/api/usuarios/**").authenticated()
                // Endpoints de administración
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());
        
        return http.build();
    }
}