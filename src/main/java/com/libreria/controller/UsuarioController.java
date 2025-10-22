package com.libreria.controller;

import com.libreria.service.UsuarioService; 
import com.libreria.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtener usuario por ID
     * Usado en ProfileView.vue para cargar datos del perfil
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cambiar contraseña del usuario autenticado
     */
    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Obtener el usuario actual
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar que el ID coincida con el usuario autenticado
            if (!usuario.getId().equals(id)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No tienes permiso para cambiar esta contraseña"
                ));
            }

            String passwordActual = passwordData.get("passwordActual");
            String passwordNueva = passwordData.get("passwordNueva");

            // Verificar que la contraseña actual sea correcta
            if (!usuarioService.verificarPassword(usuario, passwordActual)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "La contraseña actual es incorrecta"
                ));
            }

            // Cambiar la contraseña
            usuarioService.cambiarPassword(usuario, passwordNueva);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Contraseña cambiada correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error al cambiar contraseña: " + e.getMessage()
            ));
        }
    }

    /**
     * Actualizar perfil del usuario autenticado
     * Este endpoint se usa cuando el usuario completa el checkout por primera vez
     */
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(
            @RequestBody Map<String, String> datosCheckout,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Obtener el usuario actual
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar datos del checkout en el perfil
            if (datosCheckout.containsKey("telefono")) {
                usuario.setTelefono(datosCheckout.get("telefono"));
            }
            if (datosCheckout.containsKey("documento")) {
                usuario.setDocumento(datosCheckout.get("documento"));
            }
            if (datosCheckout.containsKey("direccion")) {
                usuario.setDireccion(datosCheckout.get("direccion"));
            }
            if (datosCheckout.containsKey("departamento")) {
                usuario.setDepartamento(datosCheckout.get("departamento"));
            }
            if (datosCheckout.containsKey("provincia")) {
                usuario.setProvincia(datosCheckout.get("provincia"));
            }
            if (datosCheckout.containsKey("distrito")) {
                usuario.setDistrito(datosCheckout.get("distrito"));
            }
            if (datosCheckout.containsKey("codigoPostal")) {
                usuario.setCodigoPostal(datosCheckout.get("codigoPostal"));
            }
            if (datosCheckout.containsKey("notas")) {
                usuario.setNotas(datosCheckout.get("notas"));
            }

            // Guardar cambios
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);

            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error al actualizar perfil: " + e.getMessage()
            ));
        }
    }
}