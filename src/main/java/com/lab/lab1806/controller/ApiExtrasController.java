package com.lab.lab1806.controller;

import com.lab.lab1806.entity.Producto;
import com.lab.lab1806.repository.ProductoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiExtrasController {

    private final ProductoRepository repo;

    public ApiExtrasController(ProductoRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "app",    "lab_1806",
                "ts",     System.currentTimeMillis()
        );
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<Producto> all = repo.findAll();
        double valorInventario = all.stream().mapToDouble(Producto::getPrecio).sum();
        int stockTotal = all.stream().mapToInt(Producto::getStock).sum();
        double precioPromedio = all.isEmpty() ? 0 : valorInventario / all.size();
        return Map.of(
                "totalProductos",  all.size(),
                "stockAcumulado",  stockTotal,
                "valorInventario", valorInventario,
                "precioPromedio",  precioPromedio
        );
    }
}
