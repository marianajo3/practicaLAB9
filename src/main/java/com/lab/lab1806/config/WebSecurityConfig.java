package com.lab.lab1806.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security al estilo del PPT "Clase 10.1 - WebServices
 * REST con Spring Security":
 *
 *   - httpBasic()                      (Basic Auth)
 *   - SessionCreationPolicy.STATELESS  (sin sesión/cookies)
 *   - csrf().disable()                 (API stateless)
 *   - /api/** requiere autenticación; el resto es público
 *
 * Los usuarios se crean en memoria (InMemoryUserDetailsManager) con BCrypt.
 * Los roles se usan con @PreAuthorize en los controllers.
 */
@Configuration
@EnableMethodSecurity   // habilita @PreAuthorize / @Secured
public class WebSecurityConfig {

    /**
     * Cadena de filtros principal: reglas de autorización + Basic Auth + STATELESS.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1) STATELESS: la API no guarda sesión en el servidor (PPT slide 4)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 2) CSRF off: al ser un WS no hay formularios HTML propios
            .csrf(csrf -> csrf.disable())
            // 3) Habilitar Basic Auth (PPT slide 4)
            .httpBasic(httpBasic -> {})
            // 4) Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // público: H2 console (solo dev), estáticos, páginas MVC, home
                //   → el login lo maneja el JS (form en index.html) → no aparece popup del navegador
                .requestMatchers("/", "/productos/**", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                // el resto de /api/** requiere estar autenticado (PPT slide 3)
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            // permitir frames para la consola H2
            .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }

    /**
     * PasswordEncoder para hashear/verificar contraseñas con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Dos usuarios de prueba (admin/user), según el README del lab.
     *   admin / admin123  → ROLE_ADMIN
     *   user  / user123   → ROLE_USER
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}