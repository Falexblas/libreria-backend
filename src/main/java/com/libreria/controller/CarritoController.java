package com.libreria.controller;

import com.libreria.dto.*;
import com.libreria.model.Usuario;
import com.libreria.service.CarritoService;
import com.libreria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtener carrito del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<?> obtenerCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = obtenerUsuarioActual(userDetails);
            CarritoDTO carrito = carritoService.obtenerCarrito(usuario.getId());
            
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al obtener carrito: " + e.getMessage()));
        }
    }

    /**
     * Agregar item al carrito
     */
    @PostMapping("/items")
    public ResponseEntity<?> agregarItem(
            @Valid @RequestBody AgregarItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = obtenerUsuarioActual(userDetails);
            ItemCarritoDTO item = carritoService.agregarItem(usuario.getId(), request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item agregado al carrito");
            response.put("item", item);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al agregar item: " + e.getMessage()));
        }
    }

    /**
     * Actualizar cantidad de un item
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Long itemId,
            @Valid @RequestBody ActualizarCantidadRequest request) {
        try {
            ItemCarritoDTO item = carritoService.actualizarCantidad(itemId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cantidad actualizada");
            response.put("item", item);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al actualizar cantidad: " + e.getMessage()));
        }
    }

    /**
     * Eliminar item del carrito
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> eliminarItem(@PathVariable Long itemId) {
        try {
            carritoService.eliminarItem(itemId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item eliminado del carrito");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al eliminar item: " + e.getMessage()));
        }
    }

    /**
     * Vaciar carrito
     */
    @DeleteMapping
    public ResponseEntity<?> vaciarCarrito(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = obtenerUsuarioActual(userDetails);
            carritoService.vaciarCarrito(usuario.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Carrito vaciado");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al vaciar carrito: " + e.getMessage()));
        }
    }

    /**
     * Obtener usuario actual desde UserDetails
     */
    private Usuario obtenerUsuarioActual(UserDetails userDetails) {
        return usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Crear respuesta de error
     */
    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", mensaje);
        return response;
    }
}
