package com.libreria.JWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de autenticación que incluye:
 * - Token JWT para autenticación
 * - Datos básicos del usuario (solo información segura)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    
    private String token;
    private UserDTO user;  // Encapsulamos los datos del usuario en un DTO
}
