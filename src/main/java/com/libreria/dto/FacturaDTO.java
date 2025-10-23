package com.libreria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para enviar datos de factura al frontend
 * El frontend generará el PDF con jsPDF usando estos datos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    
    // Información de la factura
    private String numeroFactura;  // Formato: F001-00000123
    private LocalDateTime fecha;
    
    // Información del cliente
    private ClienteFacturaDTO cliente;
    
    // Dirección de envío
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String telefono;
    
    // Detalles de productos
    private List<DetalleFacturaDTO> detalles;
    
    // Totales
    private BigDecimal subtotal;  // Total sin IGV
    private BigDecimal igv;       // 18% de IGV
    private BigDecimal total;     // Total con IGV
    
    // Método de pago
    private String metodoPago;
    
    // Información de la empresa (opcional, puede ser hardcoded en frontend)
    private String rucEmpresa;
    private String razonSocialEmpresa;
    private String direccionEmpresa;
}
