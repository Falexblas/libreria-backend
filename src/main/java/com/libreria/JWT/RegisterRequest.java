package com.libreria.JWT;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.libreria.model.Rol;

/**
 * DTO para el registro de nuevos usuarios
 * Solo incluye campos OBLIGATORIOS para un registro rápido
 * Los demás datos se completan en el perfil o checkout
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    // ========================================
    // CAMPOS OBLIGATORIOS PARA REGISTRO
    // ========================================
    
    private String nombre;      // Obligatorio
    private String apellido;    // Opcional pero recomendado
    private String email;       // Obligatorio
    private String password;    // Obligatorio
    
    // ========================================
    // CAMPOS OPCIONALES (No necesarios para registro)
    // Se pueden agregar después en el perfil o checkout
    // ========================================
    
    private String telefono;    // Opcional
    private String direccion;   // Opcional
    private String codigoPostal; // Opcional
    private Rol rol;            // Opcional

}