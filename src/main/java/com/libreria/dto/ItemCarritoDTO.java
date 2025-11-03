package com.libreria.dto;

import com.libreria.model.Libro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoDTO {
    private Long id;
    private Long carritoId;
    private Libro libro;  // Incluir el libro completo para mostrar en el frontend
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
