package com.libreria.controller;

import com.libreria.model.Usuario;
import com.libreria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if (usuarioService.buscarPorEmail(usuario.getEmail()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El email ya está en uso");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("errors", result.getAllErrors());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        
        // No devolver la contraseña en la respuesta
        nuevoUsuario.setPassword(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Usuario registrado exitosamente");
        response.put("usuario", nuevoUsuario);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Usuario no autenticado");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // No devolver la contraseña
        usuario.setPassword(null);
        
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(@RequestBody Usuario usuarioActualizado, 
                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Usuario no autenticado");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        Usuario usuarioExistente = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo los campos permitidos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
        
        // Solo actualizar contraseña si se proporciona una nueva
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuarioExistente.setPassword(usuarioActualizado.getPassword());
        }

        Usuario usuarioGuardado = usuarioService.actualizarUsuario(usuarioExistente);
        usuarioGuardado.setPassword(null); // No devolver la contraseña

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Perfil actualizado exitosamente");
        response.put("usuario", usuarioGuardado);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login() {
        // Este endpoint es manejado por Spring Security
        // Solo se incluye para documentación de la API
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Este endpoint es manejado por Spring Security");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Este endpoint es manejado por Spring Security
        // Solo se incluye para documentación de la API
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Sesión cerrada exitosamente");
        return ResponseEntity.ok(response);
    }
}
