package com.libreria.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ordenes")
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_pedido", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaPedido;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT 'pendiente'")
    private String estado;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;

    @Column(name = "ciudad_envio", length = 100, nullable = false)
    private String ciudadEnvio;

    @Column(name = "codigo_postal_envio", length = 10, nullable = false)
    private String codigoPostalEnvio;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    private String notas;

    @Column(name = "fecha_actualizacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion;
}
