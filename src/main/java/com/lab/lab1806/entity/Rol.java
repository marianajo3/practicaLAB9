package com.lab.lab1806.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Rol del sistema (ej. ROLE_USER, ROLE_ADMIN).
 * Spring Security ya entiende el prefijo "ROLE_" para hasRole(...).
 */
@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nombre;   // "ROLE_USER" / "ROLE_ADMIN"
}