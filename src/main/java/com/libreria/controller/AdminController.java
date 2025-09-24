package com.libreria.controller;

import com.libreria.model.Orden;
import com.libreria.model.Usuario;
import com.libreria.service.OrdenService;
import com.libreria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')") // Asegura que solo los administradores puedan acceder
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private OrdenService ordenService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Estadísticas básicas para el dashboard
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
        
        dashboard.put("totalUsuarios", usuarios.size());
        dashboard.put("totalOrdenes", ordenes.size());
        dashboard.put("mensaje", "Panel de administración - API REST");
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuarioExistente -> {
                    usuario.setId(id);
                    Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
                    return ResponseEntity.ok(usuarioActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> {
                    usuarioService.eliminarUsuario(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ordenes")
    public ResponseEntity<List<Orden>> obtenerTodasLasOrdenes() {
        List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<Orden> obtenerOrdenPorId(@PathVariable Long id) {
        return ordenService.obtenerOrdenPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/ordenes/{id}/estado")
    public ResponseEntity<Orden> actualizarEstadoOrden(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String nuevoEstado = payload.get("estado");
        return ordenService.obtenerOrdenPorId(id)
                .map(orden -> {
                    Orden ordenActualizada = ordenService.actualizarEstadoOrden(id, nuevoEstado);
                    return ResponseEntity.ok(ordenActualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
