package com.libreria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @Column(length = 9)
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    @JsonIgnore
    private Orden orden;

    @Column(name = "estado_anterior", length = 50, nullable = false)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", length = 50, nullable = false)
    private String estadoNuevo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

    @Column(name = "creado_por", length = 255, nullable = false)
    private String creadoPor;

    @Column(name = "empleado_id")
    private Long empleadoId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (id == null) {
            id = generarIdCorto();
        }
    }

    private static String generarIdCorto() {
        String chars = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            if (i == 4) {
                sb.append("-");
            }
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Transient
    public Long getOrdenId() {
        return orden != null ? orden.getId() : null;
    }
}
