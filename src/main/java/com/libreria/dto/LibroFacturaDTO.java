package com.libreria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para información del libro en la factura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibroFacturaDTO {
    private Long id;
    private String titulo;
    private String autorNombre;
    private String autorApellido;
    private String portadaUrl;
}
