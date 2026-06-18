package com.lab.lab1806.controller;

import com.lab.lab1806.entity.Producto;
import com.lab.lab1806.repository.ProductoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API REST de productos (la que pruebas en Postman).
 * Separada del ProductoController MVC (Thymeleaf) para no mezclar.
 *
 * Reglas de seguridad (además del filtro de /api/** en WebSecurityConfig):
 *   - Lecturas (GET): USER o ADMIN
 *   - Escrituras (POST/PUT/PATCH/DELETE): solo ADMIN
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    private final ProductoRepository repo;

    public ProductoApiController(ProductoRepository repo) {
        this.repo = repo;
    }

    // ============== CRUD ==============

    // 1) GET /api/productos[?page=0&size=10&sort=id,asc]
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> listar(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction dir = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortParts[0]));

        Page<Producto> pageResult = repo.findAll(pageable);
        return ResponseEntity.ok(Map.of(
                "content",       pageResult.getContent(),
                "page",          pageResult.getNumber(),
                "size",          pageResult.getSize(),
                "totalElements", pageResult.getTotalElements(),
                "totalPages",    pageResult.getTotalPages(),
                "last",          pageResult.isLast()
        ));
    }

    // 2) GET /api/productos/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3) POST /api/productos   body: {nombre, precio, stock}
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest body) {
        Producto p = Producto.builder()
                .nombre(body.nombre())
                .precio(body.precio())
                .stock(body.stock())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(p));
    }

    // 4) PUT /api/productos/{id}  (reemplazo completo)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest body) {
        return repo.findById(id).map(existing -> {
            existing.setNombre(body.nombre());
            existing.setPrecio(body.precio());
            existing.setStock(body.stock());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5) PATCH /api/productos/{id}  (actualización parcial)
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> patch(@PathVariable Long id, @RequestBody Map<String, Object> cambios) {
        return repo.findById(id).map(p -> {
            if (cambios.containsKey("nombre")) p.setNombre((String) cambios.get("nombre"));
            if (cambios.containsKey("precio")) p.setPrecio(((Number) cambios.get("precio")).doubleValue());
            if (cambios.containsKey("stock"))  p.setStock(((Number) cambios.get("stock")).intValue());
            return ResponseEntity.ok(repo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 6) DELETE /api/productos/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ============== Búsquedas / filtros ==============

    // 7) GET /api/productos/buscar?nombre=lap
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Producto> buscar(@RequestParam String nombre) {
        return repo.findByNombreContainingIgnoreCase(nombre);
    }

    // 8) GET /api/productos/stock?min=10
    @GetMapping("/stock")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Producto> stockMinimo(@RequestParam(defaultValue = "0") Integer min) {
        return repo.findByStockGreaterThanEqual(min);
    }

    // ============== Extras (mapeados aparte, ver /api/health y /api/stats) ==============
    // Van en ApiExtrasController para no hackear el path con /..

    // ============== DTO con validaciones ==============
    public record ProductoRequest(
            @NotBlank String nombre,
            @NotNull @Positive Double precio,
            @NotNull @PositiveOrZero Integer stock
    ) {}
}
