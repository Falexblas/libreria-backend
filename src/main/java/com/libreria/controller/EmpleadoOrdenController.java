package com.libreria.controller;

import com.libreria.model.Orden;
import com.libreria.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @PostMapping("/{ordenId}/tickets")
    public ResponseEntity<?> solicitarTicketCambioEstado(
            @PathVariable Long ordenId,
            @RequestBody Map<String, Object> body
    ) {
        System.out.println("DEBUG: Body recibido: " + body);
        String estadoObjetivo = (String) body.get("estadoObjetivo");
        String motivo = (String) body.getOrDefault("motivo", "");

        System.out.println("DEBUG: estadoObjetivo = " + estadoObjetivo);
        System.out.println("DEBUG: motivo = " + motivo);

        // Validar que la orden exista
        return ordenService.obtenerOrdenPorId(ordenId)
                .map(orden -> {

                    // Validar que estadoObjetivo no sea null
                    if (estadoObjetivo == null || estadoObjetivo.trim().isEmpty()) {
                        System.out.println("DEBUG: estadoObjetivo es nulo o vacío");
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                        "mensaje", "El estado objetivo es requerido."
                                ));
                    }

                    // Validar estado objetivo (enviando / entregado)
                    if (!"enviando".equalsIgnoreCase(estadoObjetivo)
                            && !"entregado".equalsIgnoreCase(estadoObjetivo)) {
                        System.out.println("DEBUG: estado inválido, recibió: " + estadoObjetivo);
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                        "mensaje", "Estado objetivo no válido. Usa 'enviando' o 'entregado'. Recibido: " + estadoObjetivo
                                ));
                    }

                    // (Opcional) validar que la orden no esté ya entregada
                    if ("entregado".equalsIgnoreCase(orden.getEstado())) {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                        "mensaje", "La orden ya está entregada, no se puede cambiar."
                                ));
                    }

                    // Generar un ticket "dummy" por ahora
                    String ticket = "TCK-" + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

                    // Aquí podrías guardar el ticket en BD si quisieras

                    System.out.println("DEBUG: Ticket generado: " + ticket);
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "ticket", ticket,
                            "mensaje", "Ticket generado correctamente."
                    ));
                })
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(Map.of("mensaje", "Orden no encontrada."))
                );
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
