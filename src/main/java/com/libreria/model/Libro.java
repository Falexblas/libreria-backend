package com.libreria.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String titulo;

    // Relación N:M con Autores a través de la tabla libros_autores
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "libros_autores",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

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

    @Column(length = 2000)  // o el tamaño que consideres necesario
private String descripcion;

    private Integer paginas;

    @Column(length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'Español'")
    private String idioma;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @Column(name = "portada_url", length = 1000)
    private String portadaUrl; 

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean destacado;

    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean activo;

    // ========================================
    // MÉTODOS HELPER PARA AUTORES
    // ========================================
    
    /**
     * Obtiene el autor principal (primer autor de la lista)
     * Para mantener compatibilidad con código existente
     */
    public Autor getAutor() {
        return autores != null && !autores.isEmpty() ? autores.get(0) : null;
    }
    
    /**
     * Establece un único autor (para compatibilidad con código existente)
     */
    public void setAutor(Autor autor) {
        if (this.autores == null) {
            this.autores = new ArrayList<>();
        }
        this.autores.clear();
        if (autor != null) {
            this.autores.add(autor);
        }
    }
    
    /**
     * Obtiene los nombres de todos los autores como String
     * Formato: "Autor1, Autor2, Autor3"
     */
    public String getAutoresNombres() {
        if (autores == null || autores.isEmpty()) {
            return "";
        }
        return autores.stream()
            .map(a -> a.getNombre() + " " + a.getApellido())
            .collect(Collectors.joining(", "));
    }
    
    /**
     * Agrega un autor a la lista
     */
    public void agregarAutor(Autor autor) {
        if (this.autores == null) {
            this.autores = new ArrayList<>();
        }
        if (autor != null && !this.autores.contains(autor)) {
            this.autores.add(autor);
        }
    }
    
    /**
     * Elimina un autor de la lista
     */
    public void eliminarAutor(Autor autor) {
        if (this.autores != null && autor != null) {
            this.autores.remove(autor);
        }
    }
}
