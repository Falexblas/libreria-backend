package com.libreria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Long id;
    private Long usuarioId;
    private List<ItemCarritoDTO> items = new ArrayList<>();
    private BigDecimal subtotal;
    private Integer cantidadTotal;
    private LocalDateTime fechaCreacion;
    private LocalDateTime modificado;
}
