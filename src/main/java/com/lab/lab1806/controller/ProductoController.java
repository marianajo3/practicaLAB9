package com.lab.lab1806.controller;

import com.lab.lab1806.entity.Producto;
import com.lab.lab1806.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller MVC (Thymeleaf). Usa el ProductoRepository (en memoria).
 */
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository repo;

    public ProductoController(ProductoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", repo.findAll());
        return "productos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "form-producto";
    }

    @PostMapping
    public String crear(@ModelAttribute Producto producto) {
        producto.setId(null); // aseguramos INSERT
        repo.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return repo.findById(id)
                .map(p -> { model.addAttribute("producto", p); return "form-producto"; })
                .orElse("redirect:/productos");
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Producto in) {
        in.setId(id);
        repo.save(in);
        return "redirect:/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/productos";
    }
}
