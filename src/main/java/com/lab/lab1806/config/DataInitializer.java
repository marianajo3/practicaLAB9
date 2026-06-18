package com.lab.lab1806.config;

import com.lab.lab1806.entity.Producto;
import com.lab.lab1806.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    /**
     * Inserta productos de ejemplo SOLO si la tabla está vacía.
     * Así, en reinicios sucesivos los datos del usuario no se duplican.
     */
    @Bean
    CommandLineRunner seedProductos(ProductoRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(Producto.builder().nombre("Laptop").precio(3500.0).stock(10).build());
                repo.save(Producto.builder().nombre("Mouse").precio(50.0).stock(100).build());
                repo.save(Producto.builder().nombre("Teclado").precio(150.0).stock(40).build());
            }
        };
    }
}
