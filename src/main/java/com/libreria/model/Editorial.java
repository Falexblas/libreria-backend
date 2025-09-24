package com.libreria.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "editoriales")
public class Editorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;
}
