package com.lab.lab1806.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Usuario de la app. La contraseña se guarda hasheada con BCrypt
 * (los INSERTs del schema.sql ya tienen los hashes generados).
 *
 * Relación N:M con Rol vía la tabla usuario_roles.
 */
@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles",
            joinColumns        = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"))
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();
}