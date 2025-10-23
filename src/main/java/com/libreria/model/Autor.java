package com.libreria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(length = 50)
    private String nacionalidad;
    
    // Relación inversa N:M con Libros (opcional, para consultas bidireccionales)
    @ManyToMany(mappedBy = "autores")
    @JsonIgnore  // Evita recursión infinita en serialización JSON
    private List<Libro> libros = new ArrayList<>();
}
