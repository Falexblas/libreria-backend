package com.libreria.controller;

import com.libreria.model.Orden;
import com.libreria.model.Usuario;
import com.libreria.service.OrdenService;
import com.libreria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Orden>> listarOrdenes(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = obtenerUsuarioActual(userDetails);
        List<Orden> ordenes = ordenService.obtenerOrdenesPorUsuario(usuario);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Obtener detalles de un pedido específico
     */
    @GetMapping("/{id}/detalles")
    public ResponseEntity<?> obtenerDetallesOrden(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = obtenerUsuarioActual(userDetails);
            return ResponseEntity.ok(ordenService.obtenerDetallesOrden(id, usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error al obtener detalles: " + e.getMessage()
            ));
        }
    }

    /**
     * Crear nueva orden desde el checkout
     * Acepta todos los datos del formulario de checkout incluyendo items del carrito
     */
    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = obtenerUsuarioActual(userDetails);
            
            // Extraer items del carrito
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
            
            // Extraer datos del payload
            String metodoPago = (String) payload.get("metodoPago");
            String direccionEnvio = (String) payload.get("direccionEnvio");
            String ciudadEnvio = (String) payload.get("ciudadEnvio");
            String codigoPostalEnvio = (String) payload.get("codigoPostalEnvio");
            String telefonoContacto = (String) payload.get("telefonoContacto");
            String notas = (String) payload.get("notas");
            
            // Datos adicionales de ubicación
            String departamento = (String) payload.get("departamento");
            String provincia = (String) payload.get("provincia");
            String distrito = (String) payload.get("distrito");
            String referencia = (String) payload.get("referencia");
            
            // Construir dirección completa
            StringBuilder direccionCompleta = new StringBuilder();
            if (direccionEnvio != null) direccionCompleta.append(direccionEnvio);
            if (referencia != null) direccionCompleta.append(" - ").append(referencia);
            if (distrito != null) direccionCompleta.append(", ").append(distrito);
            if (provincia != null) direccionCompleta.append(", ").append(provincia);
            if (departamento != null) direccionCompleta.append(", ").append(departamento);
            
            // Crear orden con items del frontend
            Orden nuevaOrden = ordenService.crearOrdenDesdeCheckout(
                usuario,
                items,
                metodoPago,
                direccionCompleta.toString(),
                ciudadEnvio != null ? ciudadEnvio : distrito,
                codigoPostalEnvio,
                telefonoContacto,
                notas
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Orden creada exitosamente",
                "ordenId", nuevaOrden.getId(),
                "orden", nuevaOrden
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error al crear la orden: " + e.getMessage()
            ));
        }
    }

    private Usuario obtenerUsuarioActual(UserDetails userDetails) {
        return usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
