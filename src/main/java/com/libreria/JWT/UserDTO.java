package com.libreria.JWT;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para datos del usuario en respuestas de autenticación
 * Solo incluye información segura y necesaria para el frontend
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    
    // NO incluimos:
    // - password (nunca)
    // - direccion (solo si el usuario lo solicita explícitamente)
    // - telefono (solo si el usuario lo solicita explícitamente)
    // - datos financieros (nunca en respuestas de auth)
}
