package com.libreria.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String respuesta;
    private List<LibroRecomendado> librosRecomendados;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LibroRecomendado {
        private Long id;
        private String titulo;
        private String autor;
        private String portadaUrl;
        private BigDecimal precio;
        private String categoria;
    }
}
