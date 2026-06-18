package com.lab.lab1806.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Endpoints JSON de prueba consumidos por jQuery.
 * Como ya no hay Spring Security ni JWT, todos devuelven OK.
 */
@RestController
@RequestMapping("/api/mensaje")
public class MensajeController {

    @GetMapping("/publico")
    public Map<String, Object> publico() {
        return Map.of(
            "mensaje", "Hola, este endpoint es público (sin auth en el pom actual).",
            "ts", LocalDateTime.now().toString()
        );
    }

    @GetMapping("/privado")
    public Map<String, Object> privado() {
        return Map.of(
            "mensaje", "Hola, accediste a un endpoint que antes requería token.",
            "ts", LocalDateTime.now().toString()
        );
    }

    @GetMapping("/admin")
    public Map<String, Object> admin() {
        return Map.of(
            "mensaje", "Hola, este endpoint antes era solo para ADMIN (ahora todos pueden).",
            "ts", LocalDateTime.now().toString()
        );
    }
}
