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


    @PostMapping
    public ResponseEntity<Orden> crearOrden(@RequestBody Map<String, String> payload, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = obtenerUsuarioActual(userDetails);
        Orden nuevaOrden = ordenService.crearOrden(
            usuario,
            payload.get("metodoPago"),
            payload.get("direccionEnvio"),
            payload.get("ciudadEnvio"),
            payload.get("codigoPostalEnvio"),
            payload.get("telefonoContacto")
        );
        return ResponseEntity.ok(nuevaOrden);
    }

    private Usuario obtenerUsuarioActual(UserDetails userDetails) {
        return usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
