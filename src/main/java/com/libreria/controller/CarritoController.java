package com.libreria.controller;

import com.libreria.model.Usuario;
import com.libreria.service.CarritoService;
import com.libreria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import com.libreria.model.Carrito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Carrito> verCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = obtenerUsuarioActual(userDetails);
        Carrito carrito = carritoService.obtenerCarritoPorUsuarioId(usuario.getId());
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Carrito> agregarAlCarrito(@RequestParam Long libroId, @RequestParam(defaultValue = "1") int cantidad, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = obtenerUsuarioActual(userDetails);
        Carrito carritoActualizado = carritoService.agregarLibroAlCarrito(usuario.getId(), libroId, cantidad);
        return ResponseEntity.ok(carritoActualizado);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Carrito> eliminarDelCarrito(@RequestParam Long libroId, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = obtenerUsuarioActual(userDetails);
        Carrito carritoActualizado = carritoService.eliminarLibroDelCarrito(usuario.getId(), libroId);
        return ResponseEntity.ok(carritoActualizado);
    }

    private Usuario obtenerUsuarioActual(UserDetails userDetails) {
        return usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
