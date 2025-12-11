package com.libreria.controller;

import com.libreria.model.Orden;
import com.libreria.model.Ticket;
import com.libreria.model.Usuario;
import com.libreria.repository.TicketRepository;
import com.libreria.repository.UsuarioRepository;
import com.libreria.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleado/ordenes")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ADMIN','EMPLEADO')")
public class EmpleadoOrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @PutMapping(path = "/{id}/estado", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarEstadoOrden(
            @PathVariable Long id,
            @RequestParam("estado") String nuevoEstado,
            @RequestParam(value = "detalles", required = false) String detalles,
            @RequestPart(value = "fotoPaquete", required = false) MultipartFile fotoPaquete,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ordenService.obtenerOrdenPorId(id)
                .map(orden -> {
                    String estadoAnterior = orden.getEstado();
                    
                    // Validar transiciones de estado permitidas
                    if (!esTransicionValida(estadoAnterior, nuevoEstado)) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Transición de estado no permitida: " + estadoAnterior + " → " + nuevoEstado);
                        return ResponseEntity.badRequest().body(error);
                    }
                    
                    Orden ordenActualizada = ordenService.actualizarEstadoOrden(id, nuevoEstado, detalles, fotoPaquete);

                    String email = userDetails != null ? userDetails.getUsername() : "";
                    Long empleadoId = usuarioRepository.findByEmail(email)
                            .map(u -> u.getId())
                            .orElse(null);

                    Ticket ticket = new Ticket();
                    ticket.setOrden(ordenActualizada);
                    ticket.setEstadoAnterior(estadoAnterior);
                    ticket.setEstadoNuevo(nuevoEstado);
                    ticket.setMotivo(detalles != null ? detalles : "");
                    ticket.setFotoUrl(ordenActualizada.getFotoPaqueteUrl());
                    ticket.setCreadoPor(email);
                    ticket.setEmpleadoId(empleadoId);
                    ticketRepository.save(ticket);

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("orden", ordenActualizada);
                    payload.put("ticketId", ticket.getId());
                    payload.put("mensaje", "Estado actualizado y ticket generado");
                    return ResponseEntity.ok(payload);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Valida que la transición de estado sea permitida
     * pendiente → enviando → entregado (solo transiciones hacia adelante)
     */
    private boolean esTransicionValida(String estadoActual, String nuevoEstado) {
        if (estadoActual == null || nuevoEstado == null) {
            return false;
        }

        // El estado no puede cambiar a sí mismo
        if (estadoActual.equals(nuevoEstado)) {
            return false;
        }

        // Si está entregado, no se puede cambiar
        if ("entregado".equals(estadoActual)) {
            return false;
        }

        // Si está en enviando, solo puede ir a entregado
        if ("enviando".equals(estadoActual)) {
            return "entregado".equals(nuevoEstado);
        }

        // Si está pendiente, solo puede ir a enviando
        if ("pendiente".equals(estadoActual)) {
            return "enviando".equals(nuevoEstado);
        }

        return false;
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<Ticket>> obtenerTickets(@PathVariable Long id) {
        List<Ticket> tickets = ticketRepository.findByOrden_IdOrderByCreatedAtDesc(id);
        return ResponseEntity.ok(tickets);
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
