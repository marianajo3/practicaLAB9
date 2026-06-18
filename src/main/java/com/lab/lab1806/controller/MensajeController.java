package com.lab.lab1806.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Endpoints JSON consumidos por jQuery desde el frontend.
 * Protegidos por Spring Security + Basic Auth (ver WebSecurityConfig).
 * El control fino de roles se hace con @PreAuthorize.
 */
@RestController
@RequestMapping("/api/mensaje")
public class MensajeController {

    /** USER o ADMIN. */
    @GetMapping("/publico")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Map<String, Object> publico() {
        return Map.of(
            "mensaje", "Hola, este endpoint es público (requiere estar autenticado).",
            "ts", LocalDateTime.now().toString()
        );
    }

    /** USER o ADMIN. */
    @GetMapping("/privado")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Map<String, Object> privado() {
        return Map.of(
            "mensaje", "Hola, accediste a un endpoint privado con tu token Basic Auth.",
            "ts", LocalDateTime.now().toString()
        );
    }

    /** Solo ADMIN. */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> admin() {
        return Map.of(
            "mensaje", "Hola, este endpoint es solo para ADMIN.",
            "ts", LocalDateTime.now().toString()
        );
    }

    /**
     * Devuelve los datos del usuario autenticado (útil para que el JS
     * muestre/oculte secciones según el rol sin tener que adivinarlo).
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Map<String, Object> me(Authentication auth) {
        return Map.of(
            "username", auth.getName(),
            "roles",    auth.getAuthorities().stream()
                            .map(a -> a.getAuthority())
                            .toList()
        );
    }
}