package com.libreria.controller;

import com.libreria.model.Libro;
import com.libreria.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*") // Para desarrollo - en producción usar URL específica del frontend
public class LibroController {

    @Autowired
    private LibroService libroService;

    
    @GetMapping
    public ResponseEntity<List<Libro>> listarLibros() {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> verLibro(@PathVariable Long id) {
        return libroService.obtenerLibroPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Libro>> librosPorCategoria(@PathVariable Long categoriaId) {
        List<Libro> libros = libroService.obtenerLibrosPorCategoria(categoriaId);
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<Libro>> librosDestacados() {
        List<Libro> libros = libroService.obtenerLibrosDestacados();
        return ResponseEntity.ok(libros);
    }

    // Métodos CRUD simplificados


    @PostMapping
    public Libro guardarLibro(@RequestBody Libro libro) {
        return libroService.guardarLibro(libro);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Long id, @RequestBody Libro libro) {
        return libroService.obtenerLibroPorId(id)
                .map(libroExistente -> {
                    libro.setId(id);
                    return ResponseEntity.ok(libroService.guardarLibro(libro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        return libroService.obtenerLibroPorId(id)
                .map(libro -> {
                    libroService.eliminarLibro(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
