package com.libreria.controller;

import com.libreria.model.Editorial;
import com.libreria.service.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editoriales")
public class EditorialController {

    @Autowired
    private EditorialService editorialService;

    @GetMapping
    public ResponseEntity<List<Editorial>> obtenerTodasLasEditoriales() {
        List<Editorial> editoriales = editorialService.obtenerTodasLasEditoriales();
        return ResponseEntity.ok(editoriales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Editorial> obtenerEditorialPorId(@PathVariable Long id) {
        return editorialService.obtenerEditorialPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Editorial> crearEditorial(@RequestBody Editorial editorial) {
        Editorial nuevaEditorial = editorialService.guardarEditorial(editorial);
        return ResponseEntity.ok(nuevaEditorial);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Editorial> actualizarEditorial(@PathVariable Long id, @RequestBody Editorial editorial) {
        return editorialService.obtenerEditorialPorId(id)
                .map(editorialExistente -> {
                    editorial.setId(id);
                    Editorial editorialActualizada = editorialService.guardarEditorial(editorial);
                    return ResponseEntity.ok(editorialActualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarEditorial(@PathVariable Long id) {
        return editorialService.obtenerEditorialPorId(id)
                .map(editorial -> {
                    editorialService.eliminarEditorial(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
