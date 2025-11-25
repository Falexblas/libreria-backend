package com.libreria.controller;

import com.libreria.model.Orden;
import com.libreria.model.Usuario;
import com.libreria.model.Libro;
import com.libreria.dto.VentasPorMesDTO;
import com.libreria.dto.TopLibroDTO;
import com.libreria.dto.CategoriaPopularDTO;
import com.libreria.repository.OrdenRepository;
import com.libreria.service.OrdenService;
import com.libreria.service.UsuarioService;
import com.libreria.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')") // Asegura que solo los administradores puedan acceder
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private LibroService libroService;
    
    @Autowired
    private OrdenRepository ordenRepository;

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

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Total de usuarios
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        estadisticas.put("totalUsuarios", usuarios.size());
        
        // Total de órdenes
        List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
        estadisticas.put("totalOrdenes", ordenes.size());
        
        // Total de libros
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        estadisticas.put("totalLibros", libros.size());
        
        // Total de ventas (suma de todos los totales de órdenes)
        BigDecimal ventasTotales = ordenes.stream()
            .map(Orden::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        estadisticas.put("ventasTotales", ventasTotales);
        
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/ordenes/recientes")
    public ResponseEntity<List<Orden>> obtenerOrdenesRecientes() {
        List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
        
        // Ordenar por fecha más reciente y tomar las últimas 10
        List<Orden> ordenesRecientes = ordenes.stream()
            .sorted((o1, o2) -> o2.getFechaPedido().compareTo(o1.getFechaPedido()))
            .limit(10)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ordenesRecientes);
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

    @GetMapping("/ordenes/{id}/detalles")
    public ResponseEntity<?> obtenerDetallesOrden(@PathVariable Long id) {
        try {
            Object detalles = ordenService.obtenerDetallesOrdenAdmin(id);
            return ResponseEntity.ok(detalles);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================================
    // Dashboard
    // ========================================

    @GetMapping("/reportes/ventas-por-mes")
    public ResponseEntity<List<VentasPorMesDTO>> obtenerVentasPorMes() {
        int anioActual = LocalDate.now().getYear();
        List<VentasPorMesDTO> ventas = ordenRepository.findVentasPorMes(anioActual);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/reportes/estado-ordenes")
    public ResponseEntity<Map<String, Long>> obtenerEstadoOrdenes() {
        List<Object[]> resultados = ordenRepository.countByEstado();
        Map<String, Long> estadoMap = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            String estado = (String) resultado[0];
            Long cantidad = (Long) resultado[1];
            estadoMap.put(estado, cantidad);
        }
        
        return ResponseEntity.ok(estadoMap);
    }

    @GetMapping("/reportes/top-libros")
    public ResponseEntity<List<TopLibroDTO>> obtenerTopLibros() {
        List<TopLibroDTO> topLibros = ordenRepository.findTopLibros();
        // Limitar a top 5
        List<TopLibroDTO> top5 = topLibros.stream()
            .limit(5)
            .collect(Collectors.toList());
        return ResponseEntity.ok(top5);
    }

    @GetMapping("/reportes/categorias-populares")
    public ResponseEntity<List<CategoriaPopularDTO>> obtenerCategoriasPopulares() {
        List<CategoriaPopularDTO> categorias = ordenRepository.findCategoriasPopulares();
        // Limitar a top 5
        List<CategoriaPopularDTO> top5 = categorias.stream()
            .limit(5)
            .collect(Collectors.toList());
        return ResponseEntity.ok(top5);
    }
}
