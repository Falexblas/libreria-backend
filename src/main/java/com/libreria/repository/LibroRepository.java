package com.libreria.repository;

import com.libreria.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    // Buscar libros activos
    List<Libro> findByActivoTrue();
    
    // Buscar libros por categoría y activos
    List<Libro> findByCategoriaIdAndActivoTrue(Long categoriaId);
    
    // Buscar libros destacados y activos
    List<Libro> findByDestacadoTrueAndActivoTrue();
}
