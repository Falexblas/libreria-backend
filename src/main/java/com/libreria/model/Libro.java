package com.libreria.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;

    @ManyToOne
    @JoinColumn(name = "editorial_id")
    private Editorial editorial;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(length = 17, unique = true)
    private String isbn;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(precision = 5, scale = 2, columnDefinition = "DECIMAL(5,2) DEFAULT 0.00")
    private BigDecimal descuento;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int stock;

    private String descripcion;

    private Integer paginas;

    @Column(length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'Español'")
    private String idioma;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @Column(name = "portada_url", length = 500)
    private String portadaUrl;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean destacado;

    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean activo;
}
