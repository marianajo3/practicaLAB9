package com.lab.lab1806.repository;

import com.lab.lab1806.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Búsqueda por nombre (case-insensitive, contiene)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Filtrar por stock mínimo
    List<Producto> findByStockGreaterThanEqual(Integer min);

    // Versión paginada de la búsqueda
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
