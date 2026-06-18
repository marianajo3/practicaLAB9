package com.lab.lab1806.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .httpBasic(b -> {})
            .authorizeHttpRequests(auth -> auth
                // públicos: home, estáticos, h2-console
                .requestMatchers("/", "/productos/**", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                // el resto de /api/** requiere estar autenticado
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}