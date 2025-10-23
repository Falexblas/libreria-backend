package com.libreria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para información del cliente en la factura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteFacturaDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String dni;  // Documento de identidad
}
