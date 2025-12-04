package com.libreria.controller;

import com.libreria.model.Orden;
import com.libreria.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleado/ordenes")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ADMIN','EMPLEADO')")
public class EmpleadoOrdenController {

    @Autowired
    private OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<Orden>> obtenerTodasLasOrdenes() {
        List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtenerOrdenPorId(@PathVariable Long id) {
        return ordenService.obtenerOrdenPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Orden> actualizarEstadoOrden(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String nuevoEstado = payload.get("estado");
        return ordenService.obtenerOrdenPorId(id)
                .map(orden -> {
                    Orden ordenActualizada = ordenService.actualizarEstadoOrden(id, nuevoEstado);
                    return ResponseEntity.ok(ordenActualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<?> obtenerDetallesOrden(@PathVariable Long id) {
        try {
            Object detalles = ordenService.obtenerDetallesOrdenAdmin(id);
            return ResponseEntity.ok(detalles);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
