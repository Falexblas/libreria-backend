package com.libreria.controller;

import com.libreria.model.Libro;
import com.libreria.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*")
public class LibroController {

    @Autowired
    private LibroService libroService;

    // ========================================
    // ENDPOINTS PÚBLICOS
    // ========================================

    /**
     * Obtener todos los libros
     * Usado en: stores/libros.js
     */
    @GetMapping
    public ResponseEntity<List<Libro>> listarLibros() {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        return ResponseEntity.ok(libros);
    }

    /**
     * Obtener libro por ID
     * Usado en: LibroDetalleView.vue
     */
    @GetMapping("/{id}")
    public ResponseEntity<Libro> verLibro(@PathVariable Long id) {
        return libroService.obtenerLibroPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========================================
    // ENDPOINTS ADMIN (Para futuro panel)
    // ========================================

    /**
     * Crear nuevo libro (Solo ADMIN)
     * Para futuro panel de administración
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Libro> crearLibro(@RequestBody Libro libro) {
        Libro nuevoLibro = libroService.guardarLibro(libro);
        return ResponseEntity.ok(nuevoLibro);
    }

    /**
     * Actualizar libro (Solo ADMIN)
     * Para futuro panel de administración
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Long id, @RequestBody Libro libro) {
        return libroService.obtenerLibroPorId(id)
                .map(libroExistente -> {
                    libro.setId(id);
                    return ResponseEntity.ok(libroService.guardarLibro(libro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar libro (Solo ADMIN)
     * Para futuro panel de administración
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        return libroService.obtenerLibroPorId(id)
                .map(libro -> {
                    libroService.eliminarLibro(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
