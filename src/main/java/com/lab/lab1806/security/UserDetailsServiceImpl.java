package com.lab.lab1806.security;

import com.lab.lab1806.entity.Usuario;
import com.lab.lab1806.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carga usuarios desde la BD (tabla usuarios + roles).
 * Spring Security lo auto-detecta porque implementa UserDetailsService.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository repo;

    public UserDetailsServiceImpl(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String[] authorities = u.getRoles().stream()
                .map(r -> r.getNombre())     // "ROLE_ADMIN" / "ROLE_USER"
                .toArray(String[]::new);

        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .disabled(u.getEnabled() != null && !u.getEnabled())
                .authorities(authorities)
                .build();
    }
}