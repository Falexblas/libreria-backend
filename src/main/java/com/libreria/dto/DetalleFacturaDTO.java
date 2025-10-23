package com.libreria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para cada producto en la factura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFacturaDTO {
    private LibroFacturaDTO libro;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
