package com.libreria.repository;

import com.libreria.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.QueryHint;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    // ✅ Buscar todos los libros activos con relaciones (optimizado con JOIN FETCH)
    @Query("SELECT DISTINCT l FROM Libro l " +
           "LEFT JOIN FETCH l.autores " +
           "LEFT JOIN FETCH l.categoria " +
           "LEFT JOIN FETCH l.editorial " +
           "WHERE l.activo = true")
    @QueryHints(@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false"))
    List<Libro> findAllActivosConRelaciones();
    
    // ✅ Buscar libros por categoría con relaciones (optimizado con JOIN FETCH)
    @Query("SELECT DISTINCT l FROM Libro l " +
           "LEFT JOIN FETCH l.autores " +
           "LEFT JOIN FETCH l.categoria " +
           "LEFT JOIN FETCH l.editorial " +
           "WHERE l.categoria.id = :categoriaId AND l.activo = true")
    @QueryHints(@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false"))
    List<Libro> findByCategoriaIdActivosConRelaciones(@Param("categoriaId") Long categoriaId);
    
    // ✅ Buscar libros destacados con relaciones (optimizado con JOIN FETCH)
    @Query("SELECT DISTINCT l FROM Libro l " +
           "LEFT JOIN FETCH l.autores " +
           "LEFT JOIN FETCH l.categoria " +
           "LEFT JOIN FETCH l.editorial " +
           "WHERE l.destacado = true AND l.activo = true")
    @QueryHints(@QueryHint(name = "hibernate.query.passDistinctThrough", value = "false"))
    List<Libro> findDestacadosActivosConRelaciones();
}
